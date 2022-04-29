package edu.singaporetech.hrapp.leave.apply

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import edu.singaporetech.hrapp.MainActivity
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.databinding.FragmentLeaveApplicationBinding
import edu.singaporetech.hrapp.leave.database.FirebaseLeaveService
import edu.singaporetech.hrapp.leave.database.LeaveRecordViewModel
import edu.singaporetech.hrapp.leave.main.LeaveData
import edu.singaporetech.hrapp.leave.main.LeaveFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * Class for LeaveApplicationFragment when a user submits wants to apply for leaves
 * @return Fragment(R.layout.fragment_leave_application)
 */
class LeaveApplicationFragment : Fragment(R.layout.fragment_leave_application) {

    private var _binding: FragmentLeaveApplicationBinding? = null
    private val binding get() = _binding!!
    val TAG = this::class.java.simpleName
    var daysDiff: Long = 0
    var startDateString: String = ""
    var endDateString : String = ""
    var leaveTypeString: String = ""
    var leaveShiftType: String = ""
    var encodedString: String = ""
    private lateinit var mViewModel: LeaveRecordViewModel
    lateinit var leaveData : MutableList<LeaveData>


        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaveApplicationBinding.inflate(inflater, container, false)
        val leaveType: Spinner = binding.spinnerLeaveType
        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.LeaveTypes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            leaveType.adapter = adapter
        }

        leaveType.onItemSelectedListener = object:
        AdapterView.OnItemSelectedListener {

            /**
             * Returns value of selected item on the spinner
             * @param parent
             * @param view
             * @param pos
             * @param id
             */
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                leaveTypeString = parent.getItemAtPosition(pos).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.DateRangeID.setOnClickListener {
            showDataRangePicker()
        }

        binding.toggleButtonGroup.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->

            if (isChecked) {
                when (checkedId) {
                    R.id.btnFull -> leaveShiftType = "FULL"
                    R.id.btnAM -> leaveShiftType = "AM"
                    R.id.btnPM -> leaveShiftType = "PM"
                }
            }
        }

        binding.uploadProofBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 3)
        }

        binding.submitLeaveAppBtn.setOnClickListener {
            //If user selects days more than available no. of leaves, toast error message
            CoroutineScope(Dispatchers.IO).launch() {
                leaveData = FirebaseLeaveService.getLeavesData() as MutableList<LeaveData>
            }


            val dialog = MaterialDialog(requireContext())
                .title(text = "Confirm Submission")
                .message(text = "Are you sure you want to submit?")
                .positiveButton(text= "Submit")
                .negativeButton(text = "Cancel")
                .positiveButton { dialog ->
                    if(leaveShiftType=="" || startDateString=="" || endDateString=="" || binding.LeaveReasonID.text.isEmpty())
                    {
                        printToast("Fill in Required Fields!")
                    }
                    else if(leaveData[0].annualleft < daysDiff && leaveTypeString=="Annual") {
                        printToast("Too little Annual leaves!")
                    }
                    else if(leaveData[0].sickleft < daysDiff && leaveTypeString=="Sick") {
                        printToast("Too little Sick leaves!")
                    }
                    else if(leaveData[0].compassionleft < daysDiff && leaveTypeString=="Compassion") {
                        printToast("Too little Compassion leaves!")
                    }
                    else if(leaveData[0].othersleft < daysDiff && leaveTypeString=="Others") {
                        printToast("Too little leaves!")
                    }

                    else {
                        mViewModel = ViewModelProvider(this).get(LeaveRecordViewModel::class.java)

                        addFireStoreLeaves(
                            leaveTypeString,
                            startDateString,
                            endDateString,
                            leaveShiftType,
                            binding.LeaveReasonID.text.toString(),
                            encodedString
                        )

                        mViewModel.updateLeave(daysDiff.toInt(),leaveTypeString)
                        printToast("Application Success!")

                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                (activity as MainActivity).replaceFragment(LeaveFragment())
                            }
                        }, 500)
                    }

                }
                .negativeButton {
                    val dialog = MaterialDialog(requireContext())
                    dialog.dismiss()
                }

            dialog.show()
            }

            val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
                /**
                 * Replaces previous fragment when back button is pressed
                 */
                override fun handleOnBackPressed() {
                    activity?.supportFragmentManager?.popBackStackImmediate()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

            activity?.setTitle("Leave Application")
            return binding.root
    }

    /**
     * Converts base64 string to image after user selects from gallery
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === AppCompatActivity.RESULT_OK && android.R.attr.data != null) {
            val selectedImage: Uri = data!!.getData()!!
            binding.proofImage.setImageURI(selectedImage)

            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImage)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray: ByteArray = outputStream.toByteArray()
            encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.d(TAG, encodedString);
        }
    }

    /**
     * Function that prints a toast message
     * @param str
     */
    private fun printToast(str: String) {
        Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()
    }

    /**
     * Function to display a date picker
     */
    private fun showDataRangePicker() {

        val constraintsBuilder = CalendarConstraints.Builder().setValidator(
            DateValidatorPointForward.now())

        val dateRangePicker =
            MaterialDatePicker
                .Builder.dateRangePicker()
                .setTitleText("Select Date")
                .setCalendarConstraints(constraintsBuilder.build())
                .build()


        dateRangePicker.show(
            parentFragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { dateSelected ->
            val startDate: Long = dateSelected.first
            val endDate: Long = dateSelected.second
            if (startDate != null && endDate != null) {
                binding.DateRangeID.text =
                    " ${convertLongToTime(startDate)}" + " - ${convertLongToTime(endDate)}"
            }
            startDateString = convertLongToTime(startDate)
            endDateString = convertLongToTime(endDate)
            val milliSecDiff = endDate - startDate
            daysDiff = TimeUnit.MILLISECONDS.toDays(milliSecDiff) + 1
            printToast(daysDiff.toString() + " Day(s) selected")
        }
    }

    /**
     * Function that converts a long value to date
     * @param time
     * @return String
     */
    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat(
            "dd.MM.yyyy",
            Locale.getDefault())
        return format.format(date)
    }

    /**
     * Function that adds a document to firebase based on the values in the fields set
     * @param leaveTypeString
     * @param startDateString
     * @param endDateString
     * @param leaveShiftType
     * @param leaveReason
     * @param ImageBase64
     */
    fun addFireStoreLeaves(leaveTypeString: String, startDateString: String, endDateString: String, leaveShiftType: String, leaveReason: String, ImageBase64: String){
        val leave: MutableMap<String,Any> = HashMap()

        leave["daytype"] = leaveShiftType
        leave["doa"] = Timestamp(Date())
        leave["file"] = ImageBase64
        leave["from"] = startDateString
        leave["remarks"] = leaveReason
        leave["status"] = "Pending"
        leave["to"] = endDateString
        leave["type"] =leaveTypeString

        mViewModel.addLeave(leave)
    }
}