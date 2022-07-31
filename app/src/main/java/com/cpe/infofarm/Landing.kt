package com.cpe.infofarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Landing : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val landingBtn: Button = findViewById (R.id.landing_button)

        landingBtn.setOnClickListener{
            val intent = Intent(this, Weather::class.java)
            startActivity(intent)
        }

    }
}