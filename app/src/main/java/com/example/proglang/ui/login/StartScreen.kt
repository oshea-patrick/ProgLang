package com.example.proglang.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.proglang.R
import com.example.proglang.api.SpotTest

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

class StartScreen : AppCompatActivity() {

    private val clientId = "f0593fe09a274cdb9ace5c6f31959336"
    private val redirectUri = "http://com.example.proglang/callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        val toLoginButtonVal = findViewById<Button>(R.id.login)
        val pairButton  = findViewById<Button>(R.id.pair)


        toLoginButtonVal.setOnClickListener {
            SpotTest.testMethod();
        }

        pairButton.setOnClickListener{

            Toast.makeText(
                applicationContext,
                "Pairing",
                Toast.LENGTH_LONG
            ).show()
        }



    }

    override fun onStart() {
        super.onStart()
            val connectionParams = ConnectionParams.Builder(clientId)
                .setRedirectUri(redirectUri)
                .showAuthView(true)
                .build()
            SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
                override fun onConnected(appRemote: SpotifyAppRemote) {
                    spotifyAppRemote = appRemote
                    Log.d("MainActivity", "Connected! Yay!")
                    // Now you can start interacting with App Remote
                    connected()
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("MainActivity", throwable.message, throwable)
                    // Something went wrong when attempting to connect! Handle errors here
                }
            })
    }

    private fun connected() {
        // Then we will write some more code here.
        spotifyAppRemote?.playerApi?.play("spotify:playlist:37i9dQZF1EpksPLJebg5Ao")
    }

    override fun onStop() {
        super.onStop()
        // Aaand we will finish off here.
    }


}
