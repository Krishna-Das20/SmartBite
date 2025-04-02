package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log // Import Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign)

        auth = Firebase.auth
        Log.d("SignActivity", "FirebaseAuth initialized: $auth") // Added Log

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        Log.d("SignActivity", "emailEditText: $emailEditText") // Added Log
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        Log.d("SignActivity", "passwordEditText: $passwordEditText") // Added Log
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        Log.d("SignActivity", "signUpButton: $signUpButton") // Added Log

        signUpButton.setOnClickListener {
            Log.d("SignActivity", "Signup button clicked! Email: ${emailEditText.text}, Password: ${passwordEditText.text}") // Added Log
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Signup success
                        Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java) // Replace MainActivity with your main activity
                        startActivity(intent)
                        finish()
                    } else {
                        // Signup failed
                        Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}