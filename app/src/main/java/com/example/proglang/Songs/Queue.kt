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
            queue?.removeAt(0)
            size--
            return temp
        }
    }

    fun peek() : Song?{
        if (size == 0)
            return null
        else {
            val temp = queue?.first()
            return temp
        }
    }

    // push without sorting
    fun add(input : Song) {
        if (size == 0 && notInitialized) {
            notInitialized = false
            queue = mutableListOf<Song>(input)
            size++
        }
        else {
            queue?.add(size, input)
            size++
        }
    }

    fun push(input : Song) {
        if (size == 0 && notInitialized) {
            notInitialized = false
            queue = mutableListOf<Song>(input)
            size++
        }
        else {
            queue?.add(size, input)
            size++
        }

        sortList()

    }

    fun printQ() {
        queue?.forEach {
            Log.d("Q", it.URI)
        }
    }


}