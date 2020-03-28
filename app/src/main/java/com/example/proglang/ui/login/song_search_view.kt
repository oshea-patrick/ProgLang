package com.example.proglang.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.adamratzman.spotify.endpoints.public.SearchApi
import com.example.proglang.R
import com.example.proglang.Songs.Song
import com.example.proglang.global.globals
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

class song_search_view : AppCompatActivity() {

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
        } catch (e: Exception) {
            Log.d("Main", "Failed")
            reConnect()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_search_view)

        val searchBar = findViewById<EditText>(R.id.searchBar)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val listView = findViewById<ListView>(R.id.searchListView)

        reConnect()

        listView.adapter =
            adapterForSearchView(
                this
            )

        searchButton.setOnClickListener {
            var result = globals.api.search.search(
                searchBar.text.toString(), SearchApi.SearchType.TRACK
            ).complete().tracks

            globals.searchList = mutableListOf<Song>()

            if (result != null) {
                var y = result.size - 1
                if (y >= 10)
                    y = 10
                for (x in 0..y) {
                    globals.searchList.add(
                        globals.searchList.size,
                        Song(result.get(x).uri.uri, globals.roomCode, 1)
                    )
                }
            }

            listView.adapter =
                adapterForSearchView(
                    this
                )
        }
    }


    private class adapterForSearchView(context: Context) : BaseAdapter() {

        private val mContext: Context


        var songs = globals.searchList

        init {
            mContext = context
        }

        override fun getCount(): Int {
            return songs.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return "Test"
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val layoutInflater = LayoutInflater.from(mContext)
            val row = layoutInflater.inflate(R.layout.row_search, parent, false)

            val button = row.findViewById<Button>(R.id.button)
            val uid = row.findViewById<TextView>(R.id.uid)
            //insert list we can filter based on search


            button.setOnClickListener {
                globals.postToSQLServer(songs.get(position).URI, globals.roomCode, 1)
            }

            val nameTextView = row.findViewById<TextView>(R.id.TitleTextView)
            nameTextView.text = songs.get(position).name + " by " + songs.get(position).artist


            return row
        }
    }
}
