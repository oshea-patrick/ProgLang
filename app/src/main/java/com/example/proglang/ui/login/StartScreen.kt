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
import com.example.proglang.SQL.getFromSQL
import com.example.proglang.Songs.Song
import com.example.proglang.global.globals
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Table
import postToSQL
import java.util.*


class StartScreen : AppCompatActivity() {
    // Connection data for API

    // Data for Queue
    var songQueue = globals.songQueue
    private var trackWasStarted = false
    var songOnQueue : Boolean = false
    var spotifyAppRemote = globals.spotifyAppRemote
    var get = globals.get
    var post = globals.post

    var roomCodeButton : TextView? = null
    var playButton  : Button? = null
    var textField : EditText? = null
    var toSongQueueButton : Button? = null


    object Songs : Table() {
        val URI = varchar("URI", 45)
        val user = varchar("user", 45)
        val numVotes = integer("numVotes")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        roomCodeButton = findViewById<TextView>(R.id.roomCode)
        playButton  = findViewById<Button>(R.id.playButton)
        textField = findViewById<EditText>(R.id.SongId)
        toSongQueueButton =  findViewById<Button>(R.id.toSongQueueButton)

        roomCodeButton?.text = globals.roomCode


        toSongQueueButton?.setOnClickListener{

            val myIntent = Intent(this, songQueueActivity::class.java)
            startActivity(myIntent)
        }


        // What to do when you add to queue
        playButton?.setOnClickListener{
            Toast.makeText(
                applicationContext,
                "Added to Queue",
                Toast.LENGTH_LONG
            ).show()

            var tempSong : Song = Song(textField?.text.toString(), "", 1)

            globals.postToSQLServer(textField?.text.toString(), globals.roomCode, 1)

            GlobalScope.launch {
                suspend {
                    globals.getFromSQLServer()
                }.invoke()
            }

            textField?.text?.clear()



            // Check and see if a song is queued up next
            if (!songOnQueue) {
                globals.nextSong = songQueue?.peek();
                spotifyAppRemote?.playerApi?.queue(globals.nextSong?.URI)
                songOnQueue = true
            }


        }
    }

    override fun onStart() {
        super.onStart()
        if (!globals.instantiated) {
            globals.roomCode = UUID.randomUUID().toString().subSequence(0,8).toString()
            // Maybe move this somewhere else later
            roomCodeButton?.text = globals.roomCode
            SpotifyAppRemote.connect(
                this,
                globals.connectionParams,
                object : Connector.ConnectionListener {
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

            // val track = api.search.searchTrack("I love college").complete().joinToString { it.uri.toString() }
        }
        else {
            spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
                handleTrackEnded(it)
            }
        }
    }

    private fun connected() {
            // Then we will write some more code here.
            val songURI = "spotify:track:1sFstGV1Z3Aw5TDFCiT7vK" //Favorite Song
            globals.instantiated = true;
            spotifyAppRemote?.playerApi?.play(songURI)
            // Subscribe to PlayerState
            spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
                val track: Track = it.track
                globals.currentSong = Song(track.uri, globals.roomCode, 1)
                handleTrackEnded(it)
            }
    }

    override fun onStop() {
        super.onStop()
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }


    // if song ends and for some reason nothing plays
    private fun handleTrackEnded(playerState: PlayerState) {
        setTrackWasStarted(playerState)
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
            val track: Track = it.track
            var tempSong = Song(track.uri, globals.roomCode, 1)

            if (!tempSong.URI.equals(globals.currentSong?.URI)) {
                // remove current song from database
                globals.removeFromSQLServer(globals.currentSong?.URI, globals.roomCode)

                // download database
                GlobalScope.launch {
                    suspend {
                        globals.getFromSQLServer()
                    }.invoke()
                }

                // update current song
                globals.currentSong = tempSong

                // update next song
                globals.nextSong = songQueue?.peek()

            }

            handleTrackEnded(it)
        }

    }

    private fun setTrackWasStarted(playerState: PlayerState) {
        val position = playerState.playbackPosition
        val duration = playerState.track.duration
        val isPlaying = !playerState.isPaused

        if (!trackWasStarted && position > 0 && duration > 0 && isPlaying) {
            trackWasStarted = true
        }
    }


}
