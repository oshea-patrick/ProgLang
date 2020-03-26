package com.example.proglang.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proglang.R
import com.example.proglang.Songs.Song
import com.example.proglang.global.globals
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class StartScreen : AppCompatActivity() {
    // Connection data for API

    // Data for Queue
    var get = globals.get
    var post = globals.post

    var roomCodeButton : TextView? = null
    var playButton  : Button? = null
    var textField : EditText? = null
    var toSongQueueButton : Button? = null
    var startButton : Button? = null

    fun reConnect() {
        try {
            SpotifyAppRemote.connect(
                this,
                globals.connectionParams,
                object : Connector.ConnectionListener {
                    override fun onConnected(appRemote: SpotifyAppRemote) {
                        globals.spotifyAppRemote = appRemote
                        Log.d("MainActivity", "Connected! Yay!")
                    }

                    override fun onFailure(throwable: Throwable) {
                        Log.e("MainActivity", throwable.message, throwable)
                    }
                })
        } catch (e : Exception) {
            Log.d("Main", "Failed")
            reConnect()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        // get access to componenets on screen
        roomCodeButton = findViewById<TextView>(R.id.roomCode)
        playButton  = findViewById<Button>(R.id.playButton)
        textField = findViewById<EditText>(R.id.SongId)
        toSongQueueButton =  findViewById<Button>(R.id.toSongQueueButton)
        startButton  = findViewById<Button>(R.id.startButton)

        // update text for screen
        roomCodeButton?.text = globals.roomCode

        // button listeners
        toSongQueueButton?.setOnClickListener{

            val myIntent = Intent(this, songQueueActivity::class.java)
            startActivity(myIntent)
        }

        startButton?.setOnClickListener{

            reConnect()

            if (globals.started) {
                globals.spotifyAppRemote?.playerApi?.pause()
                globals.started = false
                startButton?.text = "Play"
            }
            // start-up
            else if (!globals.started && globals.firstPress) {
                //globals.getFromSQLServer()
                var firstSong = globals.songQueue?.peek()
                if (firstSong != null) {
                    globals.currentSong =firstSong
                    globals.spotifyAppRemote?.playerApi?.play(firstSong?.URI)

                    globals.started = true
                    startButton?.text = "Pause"
                    globals.firstPress = false


                    SpotifyAppRemote.connect(
                        this,
                        globals.connectionParams,
                        object : Connector.ConnectionListener {
                            override fun onConnected(appRemote: SpotifyAppRemote) {
                                globals.spotifyAppRemote = appRemote
                                Log.d("MainActivity", "Connected! Yay!")
                                // Now you can start interacting with App Remote
                                connected()
                            }

                            override fun onFailure(throwable: Throwable) {
                                Log.e("MainActivity", throwable.message, throwable)
                                // Something went wrong when attempting to connect! Handle errors here
                            }
                        })

                    globals.removeFromSQLServer(firstSong.URI, globals.roomCode)

                }
            }
            else if (!globals.started && !globals.firstPress) {
                globals.spotifyAppRemote?.playerApi?.resume()
                globals.started = true
                startButton?.text = "Pause"
            }
        }

        // What to do when you add to queue
        playButton?.setOnClickListener{
            Toast.makeText(
                applicationContext,
                "Added to Queue",
                Toast.LENGTH_LONG
            ).show()

            reConnect()

            if (!textField?.text.toString().equals("")) {
                if (globals.currentSong != null && globals.nextSong == null) {
                    globals.nextSong = Song(textField?.text.toString(), globals.roomCode, 1)
                }

                globals.spotifyAppRemote?.playerApi?.queue(textField?.text.toString())
                globals.postToSQLServer(textField?.text.toString(), globals.roomCode, 1)
                globals.getFromSQLServer()


                textField?.text?.clear()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!globals.instantiated) {
            globals.roomCode = UUID.randomUUID().toString().subSequence(0,8).toString()
            // Maybe move this somewhere else later
            roomCodeButton?.text = globals.roomCode

            globals.spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {

            }

            // val track = api.search.searchTrack("I love college").complete().joinToString { it.uri.toString() }
        }
        globals.instantiated = true;
    }

    private fun connected() {
                GlobalScope.launch {
                    handleEvents()
                }
    }

    override fun onStop() {
        super.onStop()
        super.onStop()
        globals.spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }


    // if song ends and for some reason nothing plays
    suspend fun handleEvents() {
        while (true) {
            var stallTime = 10000L
            globals.spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
                val track: Track = it.track
                var playingSong = track.uri

                if (!playingSong.equals(globals.currentSong?.URI)) {

                    // remove current song from database
                    globals.removeFromSQLServer(globals.currentSong?.URI, globals.roomCode)

                    // make sure that the song that was supposed to be up next is the one playing

                    if (!playingSong.equals(globals.nextSong?.URI)) {
                        reConnect()
                        globals.spotifyAppRemote?.playerApi?.play(globals.nextSong?.URI)
                        globals.currentSong = globals.nextSong
                        globals.nextSong = null
                        globals.removeFromSQLServer(globals.currentSong?.URI, globals.roomCode)
                    }


                    // download database and update nextSong
                    globals.getFromSQLServer()
                }

                // Song finished no autoplay
                else if (playingSong.equals(globals.currentSong?.URI) && (it.playbackPosition == it.track.duration || it.playbackPosition == 0L)) {
                    globals.removeFromSQLServer(globals.currentSong?.URI, globals.roomCode)
                    reConnect()
                    globals.spotifyAppRemote?.playerApi?.play(globals.nextSong?.URI)
                    globals.currentSong = globals.nextSong
                    globals.nextSong = null
                    globals.removeFromSQLServer(globals.currentSong?.URI, globals.roomCode)

                    // download database and update nextSong
                    globals.getFromSQLServer()

                }


                stallTime = track.duration - it.playbackPosition + 1000
            }

            //delay(stallTime)
        }
    }


}
