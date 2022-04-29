package edu.singaporetech.hrapp.claim.editclaims
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import edu.singaporetech.hrapp.MainActivity
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.claim.claimrecords.Claim_RecordFragment
import edu.singaporetech.hrapp.claim.database.ClaimRecordViewModel
import edu.singaporetech.hrapp.databinding.FragmentEditClaimsBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Double
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

private lateinit var photoFile : File
private const val FILE_NAME = "photo.png"

/**
 * Class for Editing claims
 * @return Fragment(R.layout.fragment_edit_claims)
 */
class Claim_EditFragment: Fragment(R.layout.fragment_edit_claims) {
    private var _binding: FragmentEditClaimsBinding? = null
    private val binding get() = _binding!!

    var day = 0
    var month: Int = 0
    var year: Int = 0

    var selectedcategory : String = "0"

    lateinit var uploadfilebase64 : String

    var category = arrayOf<String?>("Transport", "Medical", "Allowance", "Other")

    private lateinit var mViewModel: ClaimRecordViewModel

    private var MLbitmap:Bitmap?= null

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditClaimsBinding.inflate(inflater, container, false)
        activity?.setTitle("Submit Claims")

        mViewModel = ViewModelProvider(this).get(ClaimRecordViewModel::class.java)


        var textViewSelectedDate: TextView = binding.textViewSelectedDate
        val claimType: Spinner = binding.spinner1

        // Fetches data based on fields specified in the document
        val args = this.arguments
        val itemid = args?.get("id")
        val status = args?.get("status")
        val amount = args?.get("amount")
        val category = args?.get("category")
        val dop = args?.get("dop")
        val receiptno = args?.get("receiptno")
        val remarks = args?.get("remarks")
        uploadfilebase64 = args?.get("uploadedfile").toString()
        args?.clear()

        //callback to the previous fragment
        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                activity?.supportFragmentManager?.popBackStack()
                activity?.supportFragmentManager?.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        var categoryid : Int
        if (category.toString() == "Transport"){
            categoryid = 1
        } else if ((category.toString() == "Medical")){
            categoryid = 2
        }else{
            categoryid = 3
        }

        binding.editTextAmount.setText(amount.toString())

        var newdatetime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dop.toString().toLong()), ZoneOffset.UTC)
        var formatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
        var formateddatetime = newdatetime.format(formatter)
        binding.textViewSelectedDate.setText(formateddatetime)

        binding.editTextNumberReceiptNum.setText(receiptno.toString())
        binding.editTextRemarks.setText(remarks.toString())
        val imageBytes = Base64.decode(uploadfilebase64, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        binding.imageView.setImageBitmap(decodedImage)

        activity?.setTitle("Edit Claim")

        val calendar: Calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH) + 1
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

        var pdatetime = "${year}-${monthStr}-${dayStr} 13:40:17"
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val localdatepicker = LocalDateTime.parse(pdatetime, pattern)
        val formattedpickerdatetime = localdatepicker.atZone(ZoneOffset.UTC).toEpochSecond()

        ArrayAdapter.createFromResource(requireActivity(),
            R.array.cmcategory_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            claimType.adapter = adapter
        }
        binding.spinner1.setSelection(categoryid)

        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            /**
             * Returns value of selected item on the spinner
             * @param parent
             * @param view
             * @param pos
             * @param id
             */
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position){
                    1 -> selectedcategory = "Transport"
                    2 -> selectedcategory = "Medical"
                    3 -> selectedcategory = "Allowance"
                    4 -> selectedcategory = "Others"
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
            mViewModel.updateClaim(itemid.toString()
                ,binding.editTextAmount.text.toString().toDouble()
                ,selectedcategory
                ,formattedpickerdatetime
                ,binding.editTextRemarks.text.toString()
                ,binding.editTextNumberReceiptNum.text.toString()
                ,uploadfilebase64)
            (activity as MainActivity).replaceFragment(Claim_RecordFragment())
        }

        binding.buttonClose.setOnClickListener {

            activity?.supportFragmentManager?.popBackStack()
            activity?.supportFragmentManager?.popBackStack()

        }

        return binding.root

    }

    /**
     * Function to set base64 string into an imageview
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 3 && resultCode == Activity.RESULT_OK && android.R.attr.data != null) {
            val imageTaken = BitmapFactory.decodeFile(photoFile.absolutePath)
            binding.imageView.setImageBitmap(imageTaken)

            MLbitmap = imageTaken
            val outputStream = ByteArrayOutputStream()
            imageTaken.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray: ByteArray = outputStream.toByteArray()
            val encodedString: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.d(TAG, encodedString);
            startTextRecognition()

        } else {
            val selectedImage: Uri = data!!.getData()!!
            binding.imageView.setImageURI(selectedImage)

            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImage)
            MLbitmap = bitmap
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray: ByteArray = outputStream.toByteArray()
            val encodedString: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            uploadfilebase64 = encodedString
            Log.d(TAG, encodedString);
            startTextRecognition()

        }

    }

    /**
     * Function to start Intent to choose image
     */
    private fun chooseFile(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent,2)
    }

    /**
     * Function to start intent for camera
     */
    private fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(FILE_NAME)
        val fileProvider = FileProvider.getUriForFile(requireContext(), "edu.singaporetech.firstapp.claim", photoFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)
        startActivityForResult(intent,3)
    }

    /**
     * Function that gets the file from the storage directory
     * @param fileName
     * @return File
     */
    private fun getPhotoFile(fileName: String): File {
        // use getExternalFileDir' on Context to access package-specific directories
        val storageDirectory = getActivity()?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName,".png", storageDirectory)
    }

    private fun showDialog(title: String) {
        val dialog = MaterialDialog(requireContext())
            .title(text = "Confirm Submission")
            .message(text = "Are you sure you want to submit?")
            .positiveButton(text= "Agree")
            .negativeButton(text = "Disagree")
            .positiveButton { dialog ->
                val dialog = MaterialDialog(requireContext())
                    .title(text="Successfully Submitted")
                    .positiveButton(text= "OK")
                    .positiveButton { dialog ->
                        activity?.supportFragmentManager?.popBackStackImmediate()
                    }
                dialog.show()
            }
            .negativeButton {
                val dialog = MaterialDialog(requireContext())
                dialog.dismiss()
            }

        dialog.show()
    }

    /**
     * Function for TextRecognition
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
                                        // Set amount to textview
                                        binding.editTextAmount.setText(elementText)
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
                                        binding.textViewSelectedDate.text = "$day/$month/$year"

                                    }
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