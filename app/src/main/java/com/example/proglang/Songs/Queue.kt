package com.example.proglang.Songs

import android.util.Log

class Queue constructor(){
    var size : Int = 0
    var notInitialized: Boolean = true
    var queue : MutableList<Song>? = null

    init {

    }

    fun sortList() {
        val lessSize = size - 1
        for (range in 0..lessSize) {
            var big = range;
            for (range2 in range..lessSize) {
                if (queue?.get(big)?.numVotes!! < queue?.get(range2)?.numVotes!!)
                    big = range2
            }
            var temp = queue?.get(range!!)
            queue?.set(range, queue?.get(big)!!)
            queue?.set(big, temp!!)
        }
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