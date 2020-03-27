package com.example.proglang.ui.login


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proglang.R
import com.example.proglang.global.globals
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import java.util.*

class recyclerSongQueue : AppCompatActivity() {
// TODO: i just commpied and pasted this in youll have to check it

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




    private var recyclerView: RecyclerView? = null
    private var customAdapter: SongAdapter? = null

    private val songlist = arrayOf("Apples", "Oranges", "Potatoes", "Tomatoes", "Grapes","Mangoes","Lichi", "strawberry")

// TODO: this needs to modified to list of songs
    private val model: ArrayList<Model>
        get() {
            val list = ArrayList<Model>()
            for (i in 0..7) {

                val model = Model()
                model.votes = 1
                model.name = songlist[i]
                list.add(model)
            }
            return list
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_song_queue)

        recyclerView = findViewById(R.id.songQueueRec) as RecyclerView

        modelArrayList = model
        customAdapter = SongAdapter(this)
        recyclerView!!.adapter = customAdapter
        recyclerView!!.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

    }

    companion object {
        lateinit var modelArrayList: ArrayList<Model>
    }

}



