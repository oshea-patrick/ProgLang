package com.example.proglang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.proglang.ui.login.LoginActivity

class StartScreen : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        val toLoginButtonvVal = findViewById<Button>(R.id.login)
        val pairButton  = findViewById<Button>(R.id.pair)


        toLoginButtonvVal.setOnClickListener {

            startActivity(Intent(this, LoginActivity::class.java))


        }

        pairButton.setOnClickListener{

            Toast.makeText(
                applicationContext,
                "Pairing",
                Toast.LENGTH_LONG
            ).show()
        }



    }
}
