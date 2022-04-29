package edu.singaporetech.hrapp.attendance

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import edu.singaporetech.hrapp.MainActivity
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import edu.singaporetech.hrapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Class for AttendanceFacialRecognitionActivity
 */
class AttendanceFacialRecognitionActivity: AppCompatActivity(), CoroutineScope by MainScope() {
    private val formatDate = SimpleDateFormat("dd MMM, yyyy")
    private val formatTime = SimpleDateFormat("HH:mm")

    private var CheckIn : Boolean = true

    private lateinit var userPreferencesRepository : UserPreferencesRepository

    private val currentDate = formatDate.format(Date())
    private val currentTime = formatTime.format(Date())

    private lateinit var image: InputImage
    private lateinit var bitmap: Bitmap
    private val db = Firebase.firestore

    private var options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .build()
    private val detector = FaceDetection.getClient(options)

    var location : String = ""
    var locationType : String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_facial_recognition)

        val bundle: Bundle? = intent.extras
        location = bundle?.getString("Location").toString()
        locationType = bundle?.getString("LocationType").toString()
        CheckIn = bundle?.getBoolean("CheckIn")!!

        val imageURI = Uri.parse(intent.getStringExtra("imageURI"))
        val imgView: ImageView = findViewById(R.id.capturedPhoto)
        imgView.setImageURI(imageURI)
        try{
            image = InputImage.fromFilePath(this, imageURI)
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageURI)
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Function that Rotates the bitmap
     * @param source
     * @param degrees
     * @return Bitmap
     */
    fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix, true
        )
    }

    // [Retake Photo]
    /**
     * Function for user to retake photo
     * @param view
     */
    fun onRetake(view: View){
        val intent = Intent(this, AttendanceFacialRecognitionActivity::class.java)
        startActivity(intent)
        }

    // [Next: Facial Recognition to check for face]
    /**
     * Next activity after capturing photo
     * @param view
     */
    @RequiresApi(Build.VERSION_CODES.DONUT)
    fun onNext(view: View){
        val imgView: ImageView = findViewById(R.id.capturedPhoto)

        detector.process(image)
            .addOnSuccessListener { faces ->
                if(faces.size !=0){
                    for (face in faces) {
                        val bounds = face.boundingBox
                        val paint = Paint()
                        paint.strokeWidth = 2f
                        paint.color = Color.GREEN
                        paint.style = Paint.Style.STROKE

                        var tempBitmap =
                            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.RGB_565)
                        val canvas = Canvas(tempBitmap)
                        canvas.drawBitmap(bitmap,0F,0F,null);
                        canvas.drawRect(bounds,paint)

                        tempBitmap = rotateBitmap(tempBitmap,0F)
                        imgView.setImageDrawable(BitmapDrawable(resources, tempBitmap))

                        // Database
                        val attendance = hashMapOf(
                            "location" to location,
                            "locationType" to locationType,
                            "Date" to currentDate.toString(),
                            "Time" to currentTime.toString()
                        )
                        db.collection("attendance").add(attendance)

                            userPreferencesRepository = UserPreferencesRepository(applicationContext.dataStore)
                            Log.d("Repository","$CheckIn")
                            if(CheckIn){
                                    launch {
                                        userPreferencesRepository.updateShowCompleted(false)
                                    }
                                }
                                else{
                                    launch {
                                        userPreferencesRepository.updateShowCompleted(true)
                                    }
                                }

                        // Success return to Main Activity
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        Thread.sleep(5000)
                        startActivity(intent)
                    }
                }
                else {
                    MaterialDialog(this).show {
                        title(text = "Please Retake Photo")
                        message(text = "No face was detected. Please Retake the photo, OR click help in the attendance page.")
                        positiveButton(text = "Retake"){
                            val intent = Intent(applicationContext, AttendanceCameraActivity::class.java)
                            startActivity(intent)
                        }
                        negativeButton(text = "Help"){
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
    }

}