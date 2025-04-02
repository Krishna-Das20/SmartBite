package com.example.myapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        // Using Coroutines for a delay instead of deprecated Handler
        lifecycleScope.launch {
            delay(3000) // Wait for 3 seconds
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            finish()
        }
    }
}
