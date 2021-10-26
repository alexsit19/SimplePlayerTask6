package com.example.exoplayermyui.data

import android.content.Context
import android.util.Log
import com.example.exoplayermyui.model.TrackJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class TrackResource() {

    init{
        Log.d("DEBUG", "TrackResource INIT")
    }

     fun getTracksFromJson(context: Context): List<TrackJson>? {
        val moshi = Moshi.Builder()
            .build()

        val arrayType = Types.newParameterizedType(List::class.java, TrackJson::class.java)
        val adapter: JsonAdapter<List<TrackJson>> = moshi.adapter(arrayType)

        val file = "playlist.json"

        val json = context.assets.open(file).bufferedReader().use { it.readText() }

        return adapter.fromJson(json)

    }
}