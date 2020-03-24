package com.example.proglang.ui.login

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.proglang.R
import com.example.proglang.global.globals

class songQueueActivity : AppCompatActivity() {

    var songQueue = globals.songQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_queue)


        val listView = findViewById<ListView>(R.id.songQueueListView)

        listView.adapter =
            adapterForSongQueue(
                this
            )
    }

    private class adapterForSongQueue(context: Context): BaseAdapter() {

        private val mContext: Context
        var songQueue = globals.songQueue

        private var names = mutableListOf<String>()


        init {
            mContext = context

            if (globals.nextSong != null) {
                names.add(names.size, globals.nextSong?.getName() + " by " + globals.nextSong?.getArtist());
            }

            for (song in songQueue?.queue.orEmpty()) {
                names.add(names.size, song.getName() + " by " + song.getArtist());
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

            positionTextView.text = "Song Number: $position"

            return row



        }


    }
}
