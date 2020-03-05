package com.example.proglang.api
import android.util.Log
import com.adamratzman.spotify.SpotifyApi.Companion.spotifyAppApi
import com.adamratzman.spotify.endpoints.public.SearchApi.SearchType.ALBUM
import com.adamratzman.spotify.endpoints.public.SearchApi.SearchType.TRACK

object SpotTest {

     fun testMethod() : String?{
        try {
            val api = spotifyAppApi(
                ("f0593fe09a274cdb9ace5c6f31959336"),
                ("0f8ae7ff05574cad8a1f8ee4cc4e7cbd")
            ).build()

            val track = api.search.searchTrack("I love college").complete().joinToString { it.uri.toString() }

            return track
        }
        catch (e: Exception) {
            Log.d("here", e.message);
        }

        return null
    }

}