package com.example.proglang.api
import android.util.Log
import com.adamratzman.spotify.SpotifyApi.Companion.spotifyAppApi

object SpotTest {

    fun testMethod() {
        try {
            val api = spotifyAppApi(
                ("f0593fe09a274cdb9ace5c6f31959336"),
                ("acecab1d157d426e9ef1e72e2c442a7d")
            ).build()
        }
        catch (e: Exception) {
            Log.d("here", e.message);
        }
    }

}