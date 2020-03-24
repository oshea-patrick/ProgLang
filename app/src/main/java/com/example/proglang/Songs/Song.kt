package com.example.proglang.Songs

import com.example.proglang.global.globals

class Song constructor(var URI : String, var user : String, var numVotes : Int){

    var api = globals.api

    fun equals(b: Song): Boolean {
        return b.URI == URI && user == b.user;
    }

    fun compareTo(b : Song) : Int {
        return numVotes - b.numVotes
    }

    fun getName():String {
        return api.tracks.getTrack(URI).complete()!!.name;
    }

    fun getArtist():String {
        return api.tracks.getTrack(URI).complete()!!.artists[0].name.toString();
    }

    override fun toString():String {
        return URI + " " + user + " " + numVotes
    }

}