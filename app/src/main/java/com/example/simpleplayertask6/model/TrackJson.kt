package com.example.exoplayermyui.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackJson (
    val title: String,
    val artist: String,
    val bitmapUri: String,
    val trackUri: String,
    val duration: Long
)
