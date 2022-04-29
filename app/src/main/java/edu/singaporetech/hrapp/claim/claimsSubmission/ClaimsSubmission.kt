package edu.singaporetech.hrapp.claim.claimsSubmission

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import edu.singaporetech.hrapp.MainActivity
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.claim.claimsmain.ClaimMainFragment
import edu.singaporetech.hrapp.claim.claimsmain.ClaimsData
import edu.singaporetech.hrapp.claim.database.ClaimRecordViewModel
import edu.singaporetech.hrapp.claim.database.FirebaseClaimService
import edu.singaporetech.hrapp.databinding.FragmentAddnewclaimsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Double
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.ByteArray
import kotlin.Int
import kotlin.Long
import kotlin.NumberFormatException
import kotlin.String
import kotlin.also
import kotlin.arrayOf
import kotlin.toString

private lateinit var photoFile : File
private const val FILE_NAME = "photo.png"

var day = 0
var month: Int = 0
var year: Int = 0

/**
 * Class for Claim Submission Fragment
 * @return Fragment(R.layout.fragment_addnewclaims)
 */
class ClaimsSubmission: Fragment(R.layout.fragment_addnewclaims) {
    private var _binding: FragmentAddnewclaimsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mViewModel: ClaimRecordViewModel

    var category = arrayOf<String?>("Transport", "Medical", "Allowance", "Other")

    private var MLbitmap:Bitmap?= null

    lateinit var encodedImage: String
    lateinit var scannedimageuri: Uri
    lateinit var amount: String
    var selectedcategory : String = "0"
    lateinit var claimData : MutableList<ClaimsData>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddnewclaimsBinding.inflate(inflater, container, false)
        activity?.setTitle("Submit Claims")

        mViewModel = ViewModelProvider(this).get(ClaimRecordViewModel::class.java)

        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).replaceFragment(ClaimMainFragment())
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Fetches data from arguments
        val args = this.arguments

        //Opens image and stores in imageview
        scannedimageuri = Uri.parse(args?.get("imageuri").toString())
        binding.imageView.setImageURI(scannedimageuri)

        amount = args?.get("amount").toString()
        day = args?.get("day").toString().toInt()
        month = args?.get("month").toString().toInt()
        year = args?.get("year").toString().toInt()
        binding.textViewSelectedDate.text = " $day/$month/$year"

        binding.editTextAmount.setText(amount)
        //for add new claims
        encodedImage = args?.get("encodedstring").toString()
        args?.clear()

        mViewModel = ViewModelProvider(this).get(ClaimRecordViewModel::class.java)

        var textViewSelectedDate: TextView = binding.textViewSelectedDate
        val claimType: Spinner = binding.spinner1

        val calendar: Calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)

        var monthStr : String  = "0"
        var dayStr : String  = "0"
        if(month < 10){
            monthStr = "0${month}"
        } else {
            monthStr = month.toString()
        }

        if(day <10){
            dayStr = "0${day}"
        } else {
            dayStr = day.toString()
        }

        // converting datetime into Epoch
        var pdatetime = "${year}-${monthStr}-${dayStr} 13:40:17"
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val localdatepicker = LocalDateTime.parse(pdatetime, pattern)
        val formattedpickerdatetime = localdatepicker.atZone(ZoneOffset.UTC).toEpochSecond()

        ArrayAdapter.createFromResource(requireActivity(),
            R.array.category_array,
            android.R.layout.simple_spinner_item
            ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            claimType.adapter = adapter
        }

        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position){
                    0 -> selectedcategory = "Transport"
                    1 -> selectedcategory = "Medical"
                    2 -> selectedcategory = "Allowance"
                    3 -> selectedcategory = "Others"
                }
            }
        }

        binding.buttonDate.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(requireContext(),DatePickerDialog.OnDateSetListener { view,myYear, myMonth, myDay ->
                    textViewSelectedDate.text = "" + myDay + "/" + (myMonth+1)+  "/" + myYear }, year, month+1, day)
            datePickerDialog.show()
        }

        binding.buttonChooseFile.setOnClickListener {
            val menuItemView = requireView().findViewById<View>(R.id.buttonChooseFile)
            val popupMenu: PopupMenu = PopupMenu(requireContext(), menuItemView)
            popupMenu.menuInflater.inflate(R.menu.uploadfile, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.openCamera ->
                        openCamera()
                    R.id.selectFromPhotos ->
                        chooseFile()
                }
                true
            })
            popupMenu.show()
        }


        binding.buttonSubmit.setOnClickListener {

            var successFlag = false

            CoroutineScope(Dispatchers.IO).launch() {
                claimData = FirebaseClaimService.getClaimsData() as MutableList<ClaimsData>
            }

            val dialog = MaterialDialog(requireContext())
                .title(text = "Confirm Submission")
                .message(text = "Are you sure you want to submit?")
                .positiveButton(text= "Agree")
                .negativeButton(text = "Disagree")
                .positiveButton { dialog ->

                    if(binding.editTextAmount.text.toString().isEmpty() || binding.editTextNumberReceiptNum.text.isEmpty() || selectedcategory.isEmpty() || encodedImage.isEmpty() )
                    {
                        printToast("Fill in Required Fields!")
                    }
                    else if(claimData[0].transportleft < binding.editTextAmount.text.toString().toDouble() && selectedcategory=="Transport") {
                        printToast("Not enough claim amount for Transport!")
                    }
                    else if(claimData[0].medicalleft < binding.editTextAmount.text.toString().toDouble() && selectedcategory=="Medical") {
                        printToast("Not enough claim amount for Medical!")
                    }
                    else if(claimData[0].allowanceleft < binding.editTextAmount.text.toString().toDouble() && selectedcategory=="Allowance") {
                        printToast("Not enough claim amount for Allowance!")
                    }
                    else if(claimData[0].othersleft < binding.editTextAmount.text.toString().toDouble() && selectedcategory=="Others") {
                        printToast("Not enough claim amount for Others!")
                    }
                    else {
                        successFlag = true
                        mViewModel.addFireStoreClaims(
                            binding.editTextAmount.text.toString().toDouble(),
                            selectedcategory,
                            formattedpickerdatetime,
                            binding.editTextNumberReceiptNum.text.toString(),
                            binding.editTextRemarks.text.toString(),
                            encodedImage
                        )

                        mViewModel.updateClaimData(binding.editTextAmount.text.toString().toDouble(),selectedcategory)
                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                (activity as MainActivity).replaceFragment(ClaimMainFragment())
                            }
                        }, 500)
                        printToast("Application Success!")
                    }
                    val dialog = MaterialDialog(requireContext())
                        .title(text="Successfully Submitted")
                        .positiveButton(text= "OK")
                        .positiveButton { dialog ->
                            dialog.dismiss()
                        }
                    if(successFlag == true) {
                        dialog.show()
                    }
                }
                .negativeButton {
                    val dialog = MaterialDialog(requireContext())
                    dialog.dismiss()
                }
                dialog.show()
        }

        binding.buttonClose.setOnClickListener {
            activity?.supportFragmentManager?.popBackStackImmediate()

        }

        return binding.root

    }

    /**
     * Function for setting imageview using base64 string
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // covert camera picture to base64 string
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            val imageTaken = BitmapFactory.decodeFile(photoFile.absolutePath)
               binding.imageView.setImageBitmap(imageTaken)

            val outputStream = ByteArrayOutputStream()
            imageTaken.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray: ByteArray = outputStream.toByteArray()
            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.d("take pic what",   encodedImage);
        // convert photo to base64 string
        } else {
            val selectedImage: Uri = data!!.getData()!!
            binding.imageView.setImageURI(selectedImage)
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImage)

            //For text recognition
            //assign selected image bitmap
            MLbitmap = bitmap

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray: ByteArray = outputStream.toByteArray()
            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.d(TAG, encodedImage);
            startTextRecognition()

        }

    }

    /**
     * Function that shows a toast
     * @param str
     */
    private fun printToast(str: String) {
        Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()
    }

    /**
     * Function to start intent to choose an image
     */
    private fun chooseFile(){
       val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,2)
    }

    /**
     * Function to start intent to start camera
     */
    private fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(FILE_NAME)
        val fileProvider = FileProvider.getUriForFile(requireContext(), "edu.singaporetech.hrapp.claim", photoFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)
            startActivityForResult(intent,3)
    }

    /**
     * Function to get a Photofile from storage directory
     * @param fileName
     * @return File
     */
    private fun getPhotoFile(fileName: String): File {
        // use getExternalFileDir' on Context to access package-specific directories
        val storageDirectory = getActivity()?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(fileName,".png", storageDirectory)
    }

    /**
     * Function for TextRecognition to scan receipts
     */
    private fun startTextRecognition(){
        val inputImage: InputImage = InputImage.fromBitmap(MLbitmap!!,0)
        Log.d("Machinelearning", "$MLbitmap")
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(inputImage)
            .addOnSuccessListener {
                Log.d("Machinelearning", "Successful Regonition!")

                var i = 1
                for (block in it.textBlocks) {
                    val blockText = block.text
                    // for amount
                    if (blockText.contains("SGD", false) && i == 4){
                        Log.d("Machinelearning", "Blocktext : ${blockText.toString()}")
                        for (line in block.lines) {
                            val lineText = line.text
                            Log.d("Machinelearning", "Linetext : ${lineText.toString()}")
                            for (element in line.elements) {
                                val elementText = element.text
                                var numeric = true

                                try {
                                    val num = Double.parseDouble(elementText)
                                }catch (e: NumberFormatException) {
                                    numeric = false
                                }

                                if (numeric) {
                                    // Sets Amount to text
                                    Log.d(
                                        "Machinelearning",
                                        "elementtext : ${elementText.toString()}"
                                    )
                                }
                            }
                        }
                    }
                    i += 1
                }
            }
            .addOnFailureListener {
                Log.d("Machinelearning", "Failed Regonition!", it)
            }
    }

}

