package com.example.proglang.ui.login

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proglang.R
import com.example.proglang.Songs.Song
import com.example.proglang.global.globals
import com.example.proglang.songQueueActivity
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import org.jetbrains.exposed.sql.Table
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSetMetaData


class StartScreen : AppCompatActivity() {
    // Connection data for API

    // Data for Queue
    var songQueue = globals.songQueue
    private var trackWasStarted = false
    var songOnQueue : Boolean = false
    var spotifyAppRemote = globals.spotifyAppRemote
    var get = getFromSQL()
    var post = postToSQL()


    object Songs : Table() {
        val URI = varchar("URI", 45)
        val user = varchar("user", 45)
        val numVotes = integer("numVotes")
    }


    public class getFromSQL :
        AsyncTask<String?, Void?, String>() {
        var res = ""
        val url = "jdbc:mysql://44.229.52.223:3306/data_collection"
        val user = "test_user"
        val pass = "proglang"
        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun onPostExecute(result: String) {
           Log.d("Result Printing", result)
        }
        override fun doInBackground(vararg params: String?): String {
            try {
                Class.forName("com.mysql.jdbc.Driver")
                val con = DriverManager.getConnection(url, user, pass)
                println("Databaseection success")
                var result = "Database Connection Successful\n"
                val st = con.createStatement()
                val rs =
                    st.executeQuery("select distinct URI from Table1")
                val rsmd: ResultSetMetaData = rs.metaData
                while (rs.next()) {
                    result += """
                        ${rs.getString(1)}
                        
                        """.trimIndent()
                }
                res = result
            } catch (e: Exception) {
                e.printStackTrace()
                res = e.toString()
            }
            return res
        }
    }



    public class postToSQL :
        AsyncTask<String?, Void?, String>() {
        var res = ""
        val url = "jdbc:mysql://44.229.52.223:3306/data_collection"
        val user = "test_user"
        val pass = "proglang"

        var URI : String? = null
        var usr :String? = null
        var numVotes : Int = 0

        override fun onPreExecute() {
            super.onPreExecute()
        }

        fun update(a : String?, b : String? , c : Int) {
            URI = a;
            usr = b;
            numVotes = c;
        }

        override fun onPostExecute(result: String) {
            Log.d("Result Printing", result)
        }
        override fun doInBackground(vararg param1: String?): String {
            try {
                Class.forName("com.mysql.jdbc.Driver")
                val conn = DriverManager.getConnection(url, user, pass)

                // create a sql date object so we can use it in our INSERT statement

                // the mysql insert statement
                val query =
                    (" insert into Table1 (URI, user, numVotes)"
                            + " values (?, ?, ?)")

                // create the mysql insert preparedstatement
                val preparedStmt: PreparedStatement = conn.prepareStatement(query)
                preparedStmt.setString(1, URI)
                preparedStmt.setString(2, usr)
                preparedStmt.setInt(3, numVotes)

                // execute the preparedstatement
                preparedStmt.execute()
                conn.close()
            } catch (e: java.lang.Exception) {
                Log.d("Got an exception!", e.message)
            }
            return res
        }
    }



    fun postToSQLServer(uri : String, user : String, numVotes : Int) {
        post.update(uri, user, numVotes)
        post.execute("")
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        val playButton  = findViewById<Button>(R.id.playButton)
        val textField :EditText = findViewById<EditText>(R.id.SongId)
        val toSongQueueButton =  findViewById<Button>(R.id.toSongQueueButton)


        //get.execute("")


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

            postToSQLServer(textField.text.toString(), "testUser", 1)

            songQueue.push(tempSong)
            textField.text.clear()

            // Check and see if a song is queued up next
            if (!songOnQueue) {
                globals.nextSong = songQueue?.pop();
                spotifyAppRemote?.playerApi?.queue(globals.nextSong?.URI)
                songOnQueue = true
            }

        }
    }

    override fun onStart() {
        super.onStart()
        if (!globals.instantiated) {
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
    }

    private fun connected() {
            // Then we will write some more code here.
            val songURI = "spotify:track:1sFstGV1Z3Aw5TDFCiT7vK" //Favorite Song
            globals.instantiated = true;
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
                globals.nextSong = tempSong
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
