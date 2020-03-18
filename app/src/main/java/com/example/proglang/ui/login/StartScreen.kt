package com.example.proglang.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proglang.R
import com.example.proglang.Songs.Queue
import com.example.proglang.Songs.Song
import com.example.proglang.songQueueActivity
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import org.jetbrains.exposed.sql.Table



class StartScreen : AppCompatActivity() {

    // Connection data for API
    private val clientId = "f0593fe09a274cdb9ace5c6f31959336"
    private val redirectUri = "http://com.example.proglang/callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    val connectionParams = ConnectionParams.Builder(clientId)
        .setRedirectUri(redirectUri)
        .showAuthView(true)
        .build()

    // Data for Queue
    var songQueue : Queue = Queue()
    private var trackWasStarted = false
    var songOnQueue : Boolean = false


    object Songs : Table() {
        val URI = varchar("URI", 48)
        val user = varchar("user", 45)
        val numVotes = integer("numVotes")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        val playButton  = findViewById<Button>(R.id.playButton)
        val textField :EditText = findViewById<EditText>(R.id.SongId)
        val toSongQueueButton =  findViewById<Button>(R.id.toSongQueueButton)


        toSongQueueButton.setOnClickListener{

            val myIntent = Intent(this, songQueueActivity::class.java)
            startActivity(myIntent)
        }


        // What to do when you add to queue
        playButton.setOnClickListener{
            Toast.makeText(
                applicationContext,
                "Added to Queue",
                Toast.LENGTH_LONG
            ).show()

            var tempSong : Song = Song(textField.text.toString(), "", 1)
            songQueue.push(tempSong)
            textField.text.clear()

            // Check and see if a song is queued up next
            if (!songOnQueue) {
                spotifyAppRemote?.playerApi?.queue(songQueue?.pop()?.URI)
                songOnQueue = true

            }

        }

       /* try {
            Database.connect(
                "jdbc:mysql://34.214.102.61:3306/data_collection",
                driver = "com.mysql.jdbc.Driver",
                user = "root",
                password = "proglang"
            )

            transaction {

                SchemaUtils.create(Songs)

                val songURI = Songs.insert {
                    it[URI] = "St. Petersburg"
                    it[user] = "Pat"
                    it[numVotes] = 1
                }
            }
        } catch (e : Exception) {
            Log.d("error", e.message)
        } */

    }

    override fun onStart() {
        super.onStart()

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

        /*val api = SpotifyApi.spotifyAppApi(
            ("f0593fe09a274cdb9ace5c6f31959336"),
            ("0f8ae7ff05574cad8a1f8ee4cc4e7cbd")
        ).build()

        val track = api.search.searchTrack("I love college").complete().joinToString { it.uri.toString() } */
    }

    private fun connected() {
        // Then we will write some more code here.
        val songURI = "spotify:track:1sFstGV1Z3Aw5TDFCiT7vK" //Favorite Song

        spotifyAppRemote?.playerApi?.play(songURI)
        // Subscribe to PlayerState
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
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

    private fun handleTrackEnded(playerState: PlayerState) {
        setTrackWasStarted(playerState)

        val isPaused = playerState.isPaused
        val position = playerState.playbackPosition
        val hasEnded = trackWasStarted && isPaused && position == 0L

        if (hasEnded) {
            trackWasStarted = false
            var tempSong : Song? = songQueue.pop()

            if (tempSong != null){
                spotifyAppRemote?.playerApi?.skipNext()
                spotifyAppRemote?.playerApi?.queue(tempSong?.URI)
                songOnQueue = true
            }
            else {
                spotifyAppRemote?.playerApi?.skipNext()
                songOnQueue = false
            }
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
