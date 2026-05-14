package com.example.nootepro

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nootepro.databinding.ActivityMainBinding // 1. Binding import hui

class MainActivity : AppCompatActivity() {

    // 2. Binding variable (lateinit ka matlab hai baad mein value denge)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // 3. Binding initialize ki (Layout khola)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 4. ViewCompat mein binding use ki (findViewById ki ab zaroorat nahi)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 5. Pehla Fragment load karna (Agar pehle se koi state save nahi hai)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateAccountFragment())
                .commit()
        }
    }
}