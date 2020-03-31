package com.example.proglang.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proglang.R
import com.example.proglang.global.globals
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

class songQueueActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_song_queue)

        val listView = findViewById<ListView>(R.id.songQueueListView)

        reConnect()

        listView.adapter =
            adapterForSongQueue(this)
        globals.queueListView = listView
        globals.queueContext = this
    }

    private class adapterForSongQueue(context: Context): BaseAdapter() {

        private val mContext: Context

        private var names = mutableListOf<String>()
        private var numSongs = mutableListOf<Int>()
        private var uniques = mutableListOf<String>()

        init {
            mContext = context

            for (song in globals.songQueue?.queue.orEmpty()) {
                names.add(names.size, song.name + " by " + song.artist);
                numSongs.add(numSongs.size, song.numVotes)
                uniques.add(uniques.size, song.URI)
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

            // Elements of row
            val nameTextView = row.findViewById<TextView>(R.id.TitleTextView)
            nameTextView.text = names.get(position)
            val positionTextView = row.findViewById<TextView>(R.id.positionTextView)
            positionTextView.text = "Votes: " + numSongs.get(position)
            val button = row.findViewById<Button>(R.id.button)
            val identifier = row.findViewById<TextView>(R.id.uid)
            identifier.text = uniques.get(position)
            val votes = row.findViewById<TextView>(R.id.votes)
            votes.text = "" + numSongs.get(position)

            button.setOnClickListener {
                // Update label
                Log.d("Update", "Update")
                Log.d("UID", "" + identifier.text)
                Log.d("Number", "" + positionTextView.text)
                // assumes song is still there
                globals.updateSQL(
                    "" + identifier.text,
                    globals.currentRoom,
                    (1 + (votes.text as String).toInt())
                )
                // update queue
                globals.sqlDone = false
                globals.getFromSQLServer(globals.currentRoom)
                while (!globals.sqlDone) {
                }
                // redraw activity
                positionTextView.text = "Votes: " + ("" + (numSongs.get(position) + 1))
                globals.queueListView?.adapter =
                    adapterForSongQueue(globals.queueContext!!)
            }


            return row



        }


    }
}
