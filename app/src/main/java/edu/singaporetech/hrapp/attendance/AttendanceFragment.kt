package edu.singaporetech.hrapp.attendance

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import edu.singaporetech.hrapp.MainActivity
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.databinding.FragmentCheckinBinding
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.afollestad.materialdialogs.list.listItemsSingleChoice

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")


/**
 * An activity that displays a map showing the place at the device's current location.
 */
class AttendanceFragment : Fragment(R.layout.fragment_checkin), OnMapReadyCallback {
    // Current Date
    private val sdf = SimpleDateFormat("dd MMM, yyyy")
    private val sdfTime = SimpleDateFormat("dd-MMM-yyyy HH:mm")
    private val currentDate: String = sdf.format(Date())

    private var CheckIn: Boolean = true

    private var _binding: FragmentCheckinBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPreferencesRepository : UserPreferencesRepository

    // Current Location
    private var locationCount = 1

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    // The entry point to the Places API.
    private lateinit var placesClient: PlacesClient

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null
    private var likelylocation: String = ""
    private var likelyPlaceNames: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions: Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs: Array<LatLng?> = arrayOfNulls(0)

    // [START maps_current_place_on_create]
    /**
     * Executes function upon view being created
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View?
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentCheckinBinding.inflate(inflater, container, false)

        userPreferencesRepository = UserPreferencesRepository(requireContext().applicationContext.dataStore)
        userPreferencesRepository.preferenceFlow.asLiveData().observe(viewLifecycleOwner, Observer { value ->
            CheckIn = value
            if(value){
                Log.d("Location","Live data $value")
                binding.checkInButton.text = "CHECK OUT"
                binding.checkInButton.setBackgroundResource(R.drawable.custom_button_border_red)
                binding.checkInButton.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.annual)))
            }
        })

        // [START_EXCLUDE silent]
        // Retrieve location and camera position from saved instance state.
        // [START maps_current_place_on_create_save_instance_state]
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
        // [END maps_current_place_on_create_save_instance_state]
        // [END_EXCLUDE]

        // [START_EXCLUDE silent]
        // Construct a PlacesClient
        Places.initialize(requireContext().applicationContext, "AIzaSyBtitywdYgSBVEETFacVX7Y03FZ8eh0XEc")
        placesClient = Places.createClient(requireContext().applicationContext)
        Log.d("Location","Places client initialized")

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext().applicationContext)

        // Build the map.
        // [START maps_current_place_map_fragment]
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.googleMap) as SupportMapFragment?
        try {
            mapFragment?.getMapAsync(this)
        }
        catch (e : Exception){
            Log.d("Location", "$e")
        }

        // [END maps_current_place_map_fragment]
        // [END_EXCLUDE]

        // Display Current Date
        val dateTextView: TextView = binding.dateTextView
        dateTextView.text = currentDate

        binding.checkInButton.setOnClickListener{
            onCheckIn()
        }

        binding.imageButton.setOnClickListener{
            onChangePlace()
        }

        binding.helpButton.setOnClickListener{
            onHelp()
        }

        activity?.setTitle("Attendance Overview")
        return binding.root
    }
    // [END maps_current_place_on_create]

    /**
     * Saves the state of the map when the activity is paused.
     * @param outState
     */
    // [START maps_current_place_on_save_instance_state]
    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }
    // [END maps_current_place_on_save_instance_state]

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     * @param map
     */
    // [START maps_current_place_on_map_ready]
    override fun onMapReady(map: GoogleMap) {
        Log.d("Location","Map initialized")
        this.map = map

        this.map!!.uiSettings.isScrollGesturesEnabled = false
        this.map!!.uiSettings.isZoomControlsEnabled = false

        // [START_EXCLUDE]
        // [START map_current_place_set_info_window_adapter]
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.map?.setInfoWindowAdapter(object : InfoWindowAdapter {
            // Return null here, so that getInfoContents() is called next.
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                // Inflate the layouts for the info window, title and snippet.
                val infoWindow = layoutInflater.inflate(
                    R.layout.custom_info_contents, view!!.findViewById(R.id.googleMap), false)
                val title = infoWindow.findViewById<TextView>(R.id.title)
                title.text = marker.title
                val snippet = infoWindow.findViewById<TextView>(R.id.snippet)
                snippet.text = marker.snippet
                return infoWindow
            }
        })
        // [END map_current_place_set_info_window_adapter]

        // Prompt the user for permission.
        getLocationPermission()
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()

        // Display Current place of Device
        showCurrentPlace()

        this.map!!.setOnMyLocationButtonClickListener {
            getDeviceLocation()
            showCurrentPlace()
            return@setOnMyLocationButtonClickListener true
        }

    }
    // [END maps_current_place_on_map_ready]

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    // [START maps_current_place_get_device_location]
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        Log.d("Location","Get Device Location")

        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result

                        if (lastKnownLocation != null) {
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    // [END maps_current_place_get_device_location]

    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }
    // [END maps_current_place_location_permission]

    /**
     * Handles the result of the request for location permissions.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    // [START maps_current_place_on_request_permissions_result]
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }
    // [END maps_current_place_on_request_permissions_result]

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    // [START maps_current_place_show_current_place]
    @SuppressLint("MissingPermission")
    private fun showCurrentPlace() {
        if (map == null) {
            return
        }
        if (locationPermissionGranted) {
            // Use fields to define the data types to return.
            val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // Use the builder to create a FindCurrentPlaceRequest.
            val request = FindCurrentPlaceRequest.newInstance(placeFields)

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            val placeResult = placesClient.findCurrentPlace(request)
            placeResult.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val likelyPlaces = task.result

                    // Set the count, handling cases where less than 5 entries are returned.
                    val count = if (likelyPlaces != null && likelyPlaces.placeLikelihoods.size < M_MAX_ENTRIES) {
                        likelyPlaces.placeLikelihoods.size
                    } else {
                        M_MAX_ENTRIES
                    }
                    var i = 0
                    likelylocation = ""
                    likelyPlaceNames = arrayOfNulls(count)
                    likelyPlaceAddresses = arrayOfNulls(count)
                    likelyPlaceAttributions = arrayOfNulls<List<*>?>(count)
                    likelyPlaceLatLngs = arrayOfNulls(count)
                    for (placeLikelihood in likelyPlaces?.placeLikelihoods ?: emptyList()) {
                        // Build a list of likely places to show the user.
                        likelyPlaceNames[i] = placeLikelihood.place.name
                        likelyPlaceAddresses[i] = placeLikelihood.place.address
                        likelyPlaceAttributions[i] = placeLikelihood.place.attributions
                        likelyPlaceLatLngs[i] = placeLikelihood.place.latLng

                        Log.d("Location","$likelyPlaces")
                        Log.d("Location","likelyPlaces")

                        // Determine if Employee is within Nan Yang
                        if("Nanyang" in likelyPlaceNames[i].toString()){
                            likelylocation = "SIT@Nanyang"
                        }

                        i++
                        if (i > count - 1) {
                            break
                        }
                    }

                    // Display Current Location
                    val locationTextView: TextView = binding.locationTextView
                    if(likelylocation != ""){
                        likelylocation = " Location: $likelylocation"
                        locationTextView.text = likelylocation
                    }
                    else{
                        likelylocation = " Location: ${likelyPlaceNames[0]}"
                        locationTextView.text = likelylocation
                    }

                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
//                    openPlacesDialog()
                } else {
                    Log.e(TAG, "Exception: %s", task.exception)
                }
            }
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.")

            // Add a default marker, because the user hasn't selected a place.
            map?.addMarker(MarkerOptions()
                .title(getString(R.string.default_info_title))
                .position(defaultLocation)
                .snippet(getString(R.string.default_info_snippet)))

            // Prompt the user for permission.
            getLocationPermission()
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    // [START maps_current_place_update_location_ui]
    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    // [END maps_current_place_update_location_ui]

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 16
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        // [START maps_current_place_state_keys]
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
        // [END maps_current_place_state_keys]

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5

    }

    /**
     * Function to set & change location
     * [Change Place Displayed]
     */
    fun onChangePlace(){
        // Display Current Location
        val locationTextView: TextView = binding.locationTextView
        if("SIT@Nanyang" in likelylocation){
        }
        else{
            locationCount %= likelyPlaceNames.size
            likelylocation = " Location: ${likelyPlaceNames[locationCount]}"
            locationTextView.text = likelylocation
            locationCount ++

        }
    }

    /**
     * Function when help button is clicked
     */
    private fun onHelp() {
        var emailSubject = "Facial Recognition not working"
        var currentTime = sdfTime.format(Date())

        val choices =
            listOf("Map not showing Accurate Location", "Facial Recognition not working", "Other issues")
        MaterialDialog(requireActivity()).show {
            title(text = "Please Select the Issue")
            listItemsSingleChoice(items = choices, initialSelection = 1){
                dialog, index, text ->
                emailSubject = text.toString()
                Log.d("Email","$emailSubject, $text, $dialog, $index")
            }

            positiveButton(text = "Email") {
                var emailBody = "Issue: $emailSubject \n Check in Time: $currentTime \n Additional Comments:"

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    val emailArray = arrayOf("Marytan@hrcompany.com")

                    data = Uri.parse("mailto:") // only email apps should handle this
                    putExtra(Intent.EXTRA_EMAIL, emailArray)
                    putExtra(Intent.EXTRA_SUBJECT,"Attendance: $emailSubject")
                    putExtra(Intent.EXTRA_TEXT, emailBody)
                }
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
    }

    /**
     * Function for employee check in
     * [Employee Check In]
     */
    private fun onCheckIn(){
        val radioWork: RadioButton = binding.radioWork
        val radioWFH: RadioButton = binding.radioWFH
        val radioCustomerSite: RadioButton = binding.radioCustomerSite

        val intent = Intent(requireContext(), AttendanceCameraActivity::class.java)
        if (radioWork.isChecked){
            if("SIT@Nanyang" in likelylocation){
                // Go to Facial Rec
                // Send Work to next intent
                intent.putExtra("Location", "Singapore Institution of Technology")
                intent.putExtra("LocationType","Work")
                intent.putExtra("CheckIn",CheckIn)
                startActivity(intent)
            }
            else{
                MaterialDialog(requireActivity()).show{
                    title(text = "Currently Not At SIT")
                    message(text = "This error occurred as you're currently not within SIT premises." +
                            "\n Please Click the Help button if your GPS is not locating properly")
                    positiveButton(text= "Understood")
                    startActivity(intent)
                }
            }
        }

        if (radioWFH.isChecked){
            intent.putExtra("Location", likelylocation)
            intent.putExtra("LocationType","WFH")
            startActivity(intent)
        }

        if (radioCustomerSite.isChecked){
            intent.putExtra("Location", likelylocation)
            intent.putExtra("LocationType","WFH")
            startActivity(intent)
        }
    }
}