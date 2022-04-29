package edu.singaporetech.hrapp.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import edu.singaporetech.hrapp.MainActivity
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.databinding.ActivityLoginBinding
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityLoginBinding

    lateinit var executor : Executor
    lateinit var biometricPrompt : BiometricPrompt
    lateinit var promptInfo : BiometricPrompt.PromptInfo

    /**
     * Creates LoginActivity for users to enter credentials
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Set Forget password Button
        binding.forgetPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, PasswordActivity::class.java))
        }

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this@LoginActivity, executor, object: BiometricPrompt.AuthenticationCallback(){

            //Handle errors that occur here
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }

            //Successful Authentication
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(applicationContext, "Logging in...", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

            //Failed Authentication
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Authentication Failed!", Toast.LENGTH_LONG).show()
            }
        })

        //Set up title, subtitle and description
        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Login using fingerprint or face")
            .setNegativeButtonText("Cancel")
            .build()

        //Set authentication button
        binding.authButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        auth = FirebaseAuth.getInstance()

    }

    /**
     * Login successful, starts MainActivity
     * @param view
     */
    fun login(view: View){
        val email = binding.editTextUsername.text.toString()
        val password = binding.editTextPassword.text.toString()

        if (email != "" && password != "") {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, R.string.ErrorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }
}