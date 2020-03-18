package com.example.proglang

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView

class songQueueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_queue)


        val listView = findViewById<ListView>(R.id.songQueueListView)

        listView.adapter = adapterForSongQueue(this)
    }

    private class adapterForSongQueue(context: Context): BaseAdapter() {

        private val mContext: Context

        private val names = arrayListOf<String>(
            "Pat", "Brendan", "Aaron"
        )


        init {
            mContext = context
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
