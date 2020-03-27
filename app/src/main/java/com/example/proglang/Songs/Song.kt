package com.example.proglang.Songs

import com.example.proglang.global.globals

class Song constructor(var URI : String, var user : String, var numVotes : Int){

    var name = ""
    var artist = ""

    var api = globals.api

    fun equals(b: Song): Boolean {
        return b.URI == URI && user == b.user;
    }

    fun compareTo(b : Song) : Int {
        return numVotes - b.numVotes
    }

    init {
        if (!URI.equals("")) {
            URI = globals.parseString(URI)
            name = api.tracks.getTrack(URI).complete()!!.name
            artist = api.tracks.getTrack(URI).complete()!!.artists[0].name.toString()
        }
    }


    override fun toString():String {
        return URI + " " + user + " " + numVotes
    }

}