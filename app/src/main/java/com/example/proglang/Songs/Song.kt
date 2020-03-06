package com.example.proglang.Songs

class Song constructor(var URI : String, var user : String, var numVotes : Int){

    fun equals(b: Song): Boolean {
        return b.URI == URI && user == b.user;
    }

    fun compareTo(b : Song) : Int {
        return numVotes - b.numVotes
    }

}