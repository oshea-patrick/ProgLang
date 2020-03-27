package com.example.proglang.ui.login

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getText
import com.adamratzman.spotify.utils.Language
import com.example.proglang.R
import com.example.proglang.global.globals
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlin.Unit.toString
import kotlin.coroutines.EmptyCoroutineContext.toString

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
        } catch (e : Exception) {
            Log.d("Main", "Failed")
            reConnect()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_search_view)


        val listView = findViewById<ListView>(R.id.searchListView)

        reConnect()

        listView.adapter =
            adapterForSearchView(
                this
            )
    }


//maybe this should be mad a class and called in both songQuee activit and here
    private class adapterForSearchView(context: Context): BaseAdapter() {

        private val mContext: Context

        private var names = mutableListOf<String>()
        private var numSongs = mutableListOf<Int>()

        //insert list we can filter based on search



        init {
            mContext = context

            //this needs to be new
            //

            for (song in globals.songQueue?.queue.orEmpty()) {
                names.add(names.size, song.name + " by " + song.artist)
                numSongs.add(numSongs.size, song.numVotes)
            }
        }

        override fun getCount(): Int {
            return names.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getItem(position: Int): Any {
            return "Test"
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//            val textView = TextView(mContext)
//            textView.text = "Row For list View"
//            return textView

            val layoutInflater = LayoutInflater.from(mContext)
            val row = layoutInflater.inflate(R.layout.row_queue, parent, false)

            val nameTextView = row.findViewById<TextView>(R.id.TitleTextView)
            nameTextView.text = names.get(position)

            val positionTextView = row.findViewById<TextView>(R.id.positionTextView)
            var rowSong = globals.songQueue?.queue?.get(position)

            positionTextView.text = "Votes: " + numSongs.get(position)

            val button = row.findViewById<Button>(R.id.button)

            button.setOnClickListener {
                // Update label
                if (rowSong != null) {
                    globals.updateSQL(rowSong?.URI, globals.roomCode, (1 + rowSong?.numVotes!!))
                }
                // update queue
                globals.getFromSQLServer()
                // redraw activity
                rowSong = globals.songQueue?.queue?.get(position)
                positionTextView.text = "Votes: " + numSongs.get(position)
            }


            return row



        }

    //TODO: update the list after search
    fun updateList(filteredList: List<>) {
        list.clear()
        list.addAll(filteredList)
        notifyDataSetChanged()
    }

//TODO: search the list we need two list one with all the songs one after filter

    fun searchList() {
        filteredList.clear()
        baseList.forEach {
            if (Language.it.contains(song_search_view.getText())) {
                filteredList.add(Language.it)
            }
        }
        adapter.updateList(filteredList)


    }
}
