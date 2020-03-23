package com.example.proglang.global

import com.adamratzman.spotify.SpotifyApi
import com.example.proglang.Songs.Queue
import com.example.proglang.Songs.Song
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote

object globals {

    var instantiated = false;
    var songQueue : Queue = Queue()
    val api = SpotifyApi.spotifyAppApi(
        ("f0593fe09a274cdb9ace5c6f31959336"),
        ("0f8ae7ff05574cad8a1f8ee4cc4e7cbd")
    ).build()
    var nextSong :Song? = null
    private val clientId = "f0593fe09a274cdb9ace5c6f31959336"
    private val redirectUri = "http://com.example.proglang/callback"
    var spotifyAppRemote: SpotifyAppRemote? = null
    val connectionParams = ConnectionParams.Builder(clientId)
        .setRedirectUri(redirectUri)
        .showAuthView(true)
        .build()

}