package com.example.exoplayermyui.data

import android.content.Context
import com.example.exoplayermyui.model.TrackJson

interface Repository {
    suspend fun getTracks(): List<TrackJson>?
}
class TrackRepository(
    private val trackResource: TrackResource,
    private val context: Context
) : Repository {

    override suspend fun getTracks(): List<TrackJson>? {
        return trackResource.getTracksFromJson(context)
    }
}
