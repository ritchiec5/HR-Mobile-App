package edu.singaporetech.hrapp.claim.claimsmain

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import edu.singaporetech.hrapp.MainActivity
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.claim.claimrecords.Claim_RecordFragment
import edu.singaporetech.hrapp.claim.claimrecords.Records
import edu.singaporetech.hrapp.claim.claimsSubmission.ClaimsSubmission
import edu.singaporetech.hrapp.claim.database.ClaimRecordViewModel
import edu.singaporetech.hrapp.databinding.FragmentClaimsoverviewBinding
import java.io.ByteArrayOutputStream
import java.lang.Double
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.Any
import kotlin.Boolean
import kotlin.ByteArray
import kotlin.Int
import kotlin.NumberFormatException
import kotlin.String
import kotlin.toString

private var MLbitmap:Bitmap?= null
private var encodedImage: String?= null
private var imageurlforadd : Uri?= null

/**
 * Class for the Main fragment in the Claims feature
 * @return Fragment(R.layout.fragment_claimsoverview)
 */
class ClaimMainFragment : Fragment(R.layout.fragment_claimsoverview){

    private var _binding: FragmentClaimsoverviewBinding? = null
    private val binding get() = _binding!!

    lateinit var claimsData: MutableList<ClaimsData>

    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var mViewModel: ClaimRecordViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        _binding = FragmentClaimsoverviewBinding.inflate(inflater, container, false)
        activity?.setTitle("Claims Overview")
        return binding.root

    }

    /**
     * Enables options menu in the fragment
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    /**
     * Function that Inflates menu options
     * @param menu
     * @param inflater
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.claims_header, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     *  Function that returns the boolean value of options selected
     *  @param item
     *  @return Boolean
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item!!.itemId

        if(id == R.id.ScanAReceipt){
            chooseFile()
        }

        if (id == R.id.ManualSubmission) {
            val bundle = Bundle()
            bundle.putString("amount", "0")
            bundle.putString("year", "0")
            bundle.putString("month", "0")
            bundle.putString("day", "0")

            val fragment = ClaimsSubmission()
            fragment.arguments = bundle

            (activity as MainActivity).replaceFragment(fragment)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === AppCompatActivity.RESULT_OK && android.R.attr.data != null) {

            val selectedImage: Uri = data!!.getData()!!
            imageurlforadd = selectedImage
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImage)
            MLbitmap = bitmap
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray: ByteArray = outputStream.toByteArray()
            val encodedString: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            encodedImage = encodedString
            Log.d(ContentValues.TAG, encodedString);
            startTextRecognition()
        }
    }

    private fun chooseFile(){
        Log.d("image", "chosen image")
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent,2)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.listClaimsOverviewRecords
        layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = layoutManager

        val adapter = ClaimMainFragmentAdapter()
        recyclerView.adapter = adapter

        val claimObserver = Observer<List<Records>> { claims ->
            // Updates the UI
            adapter.setData(claims as ArrayList<Records>)

        }

        val claimDataObserver = Observer<Any> { data ->
            adapter.setClaimsData(data as ArrayList<ClaimsData>)
            claimsData = adapter.getClaimsData()
            setData()
        }

        mViewModel = ViewModelProvider(this).get(ClaimRecordViewModel::class.java)
        mViewModel.getClaimRecord.observe(viewLifecycleOwner, claimObserver)
        mViewModel.getClaimsData.observe(viewLifecycleOwner, claimDataObserver)


        binding.buttonViewHistory.setOnClickListener {
            (activity as MainActivity).replaceFragment(Claim_RecordFragment())
        }
    }

    /**
     * Function that calculates the amount of claims translatable to the progress bars
     * @param value
     * @param total
     * @return Int
     */
    private fun calculateAmount(value: Int, total: Int): Int {
        return ((value.toDouble() / total) * 100).toInt()
    }

    /**
     * Sets data of the remaining claim amount onto the progress bars
     */
    private fun setData() {
        // Sets amount
        binding.totalPaidValue.text = claimsData[0].paid.toString()
        binding.totalApprovedValue.text = claimsData[0].approved.toString()
        binding.totalRejectedValue.text = claimsData[0].rejected.toString()
        binding.totalPendingValue.text = claimsData[0].pending.toString()

        // Gets the total amount for each type of claim
        var totalTransport = claimsData[0].transport.toInt()
        var totalAllowance = claimsData[0].allowance.toInt()
        var totalMedical = claimsData[0].medical.toInt()
        var totalOthers = claimsData[0].others.toInt()

        // Gets amount left for each type of claim
        var currentTransport = claimsData[0].transportleft.toInt()
        var currentAllowance = claimsData[0].allowanceleft.toInt()
        var currentMedical = claimsData[0].medicalleft.toInt()
        var currentOthers = claimsData[0].othersleft.toInt()

        // Sets text of the amount left of claims
        binding.textViewAllowanceLeft.text = "$$currentAllowance/$$totalAllowance left"
        binding.textViewMedicalLeft.text = "$$currentMedical/$$totalMedical left"
        binding.textViewTransportLeft.text = "$$currentTransport/$$totalTransport left"
        binding.textViewOthersLeft.text = "$$currentOthers/$$totalOthers left"

        // Sets progress bars of the claim amounts
        binding.progressBarAllowance.progress = calculateAmount(currentAllowance, totalAllowance)
        binding.progressBarMedical.progress = calculateAmount(currentMedical, totalMedical)
        binding.progressBarTransport.progress = calculateAmount(currentTransport, totalTransport)
        binding.progressBarOthers.progress = calculateAmount(currentOthers, totalOthers)
    }

    /**
     * Function for text recognition algorithm
     */
    private fun startTextRecognition(){
        val inputImage: InputImage = InputImage.fromBitmap(MLbitmap!!,0)
        Log.d("Machinelearning", "$MLbitmap")
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(inputImage)
            .addOnSuccessListener {
                Log.d("Machinelearning", "Successful Regonition!")

                var i = 1
                var price : String = "0"
                var date : String = "0"
                var year : Int = 0
                var month : Int = 0
                var day : Int = 0

                for (block in it.textBlocks) {
                    val blockText = block.text
                    if (i == 4 || i == 5){
                        for (line in block.lines) {
                            val lineText = line.text
                            if (lineText.contains("SGD", true)){
                                for (element in line.elements) {
                                    val elementText = element.text



                                    var numeric = true
                                    try {
                                        val num = Double.parseDouble(elementText)
                                    }catch (e: NumberFormatException) {
                                        numeric = false
                                    }
                                    if (numeric) {
                                        // Sets the text amount
                                        price = elementText
                                    }
                                }
                            }

                            if (lineText.contains("-", true)){
                                for (element in line.elements) {
                                    val elementText = element.text
                                    if (elementText.contains("-", true)) {

                                        var formatdate = LocalDate.parse(elementText, DateTimeFormatter.ISO_DATE)
                                        year = formatdate.year
                                        month = formatdate.monthValue
                                        day = formatdate.dayOfMonth
                                        date = formatdate.toString()
                                    }
                                }
                            }

                        }
                    }
                    i += 1
                }

                if (price != "0" && date != "0"){
                    val bundle = Bundle()
                    bundle.putString("amount", price)
                    bundle.putString("year", year.toString())
                    Log.d("testdate", year.toString())
                    bundle.putString("month", month.toString())
                    bundle.putString("day", day.toString())
                    bundle.putString("encodedstring", encodedImage)
                    bundle.putString("imageuri", imageurlforadd.toString())

                    val fragment = ClaimsSubmission()
                    fragment.arguments = bundle

                    (activity as MainActivity).replaceFragment(fragment)
                } else {
                    Toast.makeText(requireContext(),"this is toast message",Toast.LENGTH_SHORT).show()

                    val toast = Toast.makeText(requireContext(), "Receipt recognition failed. Please select manual submission", Toast.LENGTH_SHORT)
                    toast.show()
                }

            }
            .addOnFailureListener {
                Log.d("Machinelearning", "Failed Regonition!", it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
