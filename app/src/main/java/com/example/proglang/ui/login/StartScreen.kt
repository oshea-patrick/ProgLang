package com.example.proglang.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proglang.R
import com.example.proglang.global.globals
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import java.util.*


class StartScreen : AppCompatActivity() {
    // Connection data for API

    // Data for Queue
    var get = globals.get
    var post = globals.post

    var roomCodeButton : TextView? = null
    var toSongQueueButton : Button? = null
    var startButton : Button? = null
    var toSearchButton: Button? = null
    var currentRoom: EditText? = null

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

    fun playSong(uri: String) {
        try {
            SpotifyAppRemote.connect(
                this,
                globals.connectionParams,
                object : Connector.ConnectionListener {
                    override fun onConnected(appRemote: SpotifyAppRemote) {
                        globals.spotifyAppRemote = appRemote
                        Log.d("URI", "-" + uri + "-")
                        globals.spotifyAppRemote?.playerApi?.play(uri)
                    }

                    override fun onFailure(throwable: Throwable) {
                        Log.e("MainActivity", throwable.message, throwable)
                    }
                })
        } catch (e: Exception) {
            Log.d("Main", "Failed Playing Song")
            reConnect()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        // get access to componenets on screen
        roomCodeButton = findViewById<TextView>(R.id.roomCode)
        toSongQueueButton =  findViewById<Button>(R.id.toSongQueueButton)
        startButton  = findViewById<Button>(R.id.startButton)
        toSearchButton = findViewById<Button>(R.id.searchButton)
        currentRoom = findViewById<EditText>(R.id.curRoom)

        // update text for screen
        roomCodeButton?.text = globals.roomCode
        currentRoom?.setText(globals.currentRoom)

        // button listeners

        toSearchButton?.setOnClickListener {
            val myIntent = Intent(this, song_search_view::class.java)
            startActivity(myIntent)
        }

        currentRoom?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                globals.currentRoom = "" + currentRoom?.text
                true
            } else {
                false
            }
        }


        toSongQueueButton?.setOnClickListener{

            val myIntent = Intent(this, songQueueActivity::class.java)

            // make sure new data is retrieved
            globals.sqlDone = false
            globals.getFromSQLServer(globals.currentRoom)
            while (!globals.sqlDone) {
            }

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

                globals.sqlDone = false
                globals.getFromSQLServer(globals.roomCode)
                while (!globals.sqlDone) {
                }

                var firstSong = globals.songQueue?.peek()
                if (firstSong != null) {
                    globals.currentSong =firstSong
                    reConnect()
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
    }

    override fun onStart() {
        super.onStart()
        if (!globals.instantiated) {
            globals.roomCode = UUID.randomUUID().toString().subSequence(0,8).toString()
            // Maybe move this somewhere else later
            roomCodeButton?.text = globals.roomCode
            globals.currentRoom = globals.roomCode
            currentRoom?.setText(globals.roomCode)
        }
        globals.instantiated = true;
    }

    private fun connected() {


        globals.spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
            var track = it.track

            var numTimes = 0
            while (!globals.currentSong?.URI.equals(track.uri)) {
                track = it.track
                numTimes++
                if (numTimes >= 500)
                    break
            }
        }
        // disconnect listener
        SpotifyAppRemote.disconnect(globals.spotifyAppRemote)
        reConnect()

        globals.mainHandler.post(object : Runnable {
            override fun run() {
                handleEvents()
                globals.mainHandler.postDelayed(this, 5000)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        super.onStop()
        globals.spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }


    fun handleEvents() {
        globals.numThreads++
        Log.d("Num Threads", "" + globals.numThreads)

        globals.getFromSQLServer(globals.roomCode)

        globals.spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {

            // get currently playing song
            var currentlyPlaying = it.track.uri

            // if the incorrect song
            if (!currentlyPlaying.equals(globals.currentSong?.URI) || (it.playbackPosition == 0L)) {

                if (globals.currentSong == null && globals.nextSong == null) {
                    globals.spotifyAppRemote?.playerApi?.pause()
                    startButton?.text = "Play"
                    globals.started = false
                    globals.firstPress = true
                }


                if (!currentlyPlaying.equals(globals.nextSong?.URI)) {


                    // fix the songs
                    if (globals.nextSong != null) {
                        globals.currentSong = globals.nextSong
                    } else {
                        Log.d("Next Null", "Null")
                    }

                    // play the song
                    playSong("" + globals.currentSong?.URI)

                    //Log.d("Currently playing", currentlyPlaying)
                    //Log.d("Should be", globals.currentSong?.URI)


                    // make sure it plays
                    var numRun = 0
                    globals.spotifyAppRemote?.playerApi?.subscribeToPlayerState()
                        ?.setEventCallback {
                            while (!it.track.uri.equals(globals.currentSong?.URI)) {
                                numRun++
                                if (numRun >= 500)
                                    break
                            }
                    }

                    // remove the next song from queue
                    globals.removeFromSQLServer(globals.currentSong?.URI, globals.roomCode)


                }

            } else {
                globals.nextTimeChange = true
            }

        }

        SpotifyAppRemote.disconnect(globals.spotifyAppRemote)
        reConnect()
        globals.numThreads--
    }


}
