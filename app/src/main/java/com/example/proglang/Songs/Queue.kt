package com.example.proglang.Songs

import android.util.Log
import java.util.*

class Queue constructor(){
    var size : Int = 0
    var notInitialized: Boolean = true
    var queue : MutableList<Song>? = null

    init {

    }

    fun sortList() {
        queue?.sortedByDescending { Song -> Song.numVotes }
    }

    fun pop() : Song?{
        if (size == 0)
            return null
        else {
            val temp = queue?.first()
            Log.d("Song", "Popped song")
            queue?.removeAt(0)
            size--
            return temp
        }
    }

    fun push(input : Song) {
        if (size == 0 && notInitialized) {
            notInitialized = false
            queue = mutableListOf<Song>(input)
            Log.d("Song", "Pushed song, size was zero")
            size++
        }
        else {
            queue?.add(size, input)
            size++
            Log.d("Song", "Pushed song")
        }

        sortList()

    }


}