package com.example.proglang.ui.login

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proglang.R

import org.w3c.dom.Text

class SongAdapter(private val ctx: Context) : RecyclerView.Adapter<SongAdapter.MyViewHolder>(){

    private val inflater: LayoutInflater

    init {

        inflater = LayoutInflater.from(ctx)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongAdapter.MyViewHolder {

        val view = inflater.inflate(R.layout.row_queue, parent, false)

        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: SongAdapter.MyViewHolder, position: Int) {
        holder.songTitle.setText(recyclerSongQueue.modelArrayList.get(position).name)
        holder.voteNum.setText(recyclerSongQueue.modelArrayList.get(position).votes.toString())
    }


    override fun getItemCount(): Int {
        return recyclerSongQueue.modelArrayList.size
    }




    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        protected var upVote: Button
        val songTitle: TextView
        val voteNum: TextView

        init {
            songTitle = itemView.findViewById(R.id.TitleTextView) as TextView
            voteNum = itemView.findViewById(R.id.voteTextView) as TextView
            upVote = itemView.findViewById(R.id.btn_up_vote) as Button

            upVote.setTag(R.integer.ButtonUP, itemView)
            upVote.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val tempview = upVote.getTag(R.integer.ButtonUP) as View
            val tv = tempview.findViewById(R.id.voteTextView) as TextView
            val number: Int = Integer.parseInt(tv.text.toString()) + 1
            tv.text = number.toString()
            recyclerSongQueue.modelArrayList.get(adapterPosition).votes = number
        }




    }


}