package com.example.proglang.global

import android.util.Log
import com.adamratzman.spotify.SpotifyApi
import com.example.proglang.SQL.getFromSQL
import com.example.proglang.Songs.Queue
import com.example.proglang.Songs.Song
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote
import postToSQL
import java.lang.Exception

object globals {

    var instantiated = false;
    var firstPress = true;
    var songQueue : Queue? = Queue()
    val api = SpotifyApi.spotifyAppApi(
        ("f0593fe09a274cdb9ace5c6f31959336"),
        ("0f8ae7ff05574cad8a1f8ee4cc4e7cbd")
    ).build()
    var currentSong : Song? = null
    var nextSong :Song? = null
    private val clientId = "f0593fe09a274cdb9ace5c6f31959336"
    private val redirectUri = "http://com.example.proglang/callback"
    var spotifyAppRemote: SpotifyAppRemote? = null
    val connectionParams = ConnectionParams.Builder(clientId)
        .setRedirectUri(redirectUri)
        .showAuthView(true)
        .build()
    var started = false
    var roomCode = ""
    var get = getFromSQL()
    var post = postToSQL()

    fun postToSQLServer(uri : String, user : String, numVotes : Int) {
        try {
            post.update(uri, user, numVotes)
            post.execute("")
        } catch (e : Exception) {
            Log.d("Post Crashed", e.message)
        }
        post = postToSQL()
    }

    fun removeFromSQLServer(uri : String?, user : String) {
        try {
            post.update(uri, user, -999)
            post.execute("")
        } catch (e : Exception) {
            Log.d("Remove Crashed", e.message)
        }
        post = postToSQL()
    }

    fun getFromSQLServer() {
        try {
            get.update(globals.roomCode)
            get.execute("")
        } catch (e : Exception) {
            Log.d("Get Crashed", e.message)
        }
    get = getFromSQL()
    }

    fun parseString(a : String) :String {
        var out = ""
        for (char in a) {
            if (char != ' ' && char != '\n') {
                out += char
            }
        }
        return out
    }

}