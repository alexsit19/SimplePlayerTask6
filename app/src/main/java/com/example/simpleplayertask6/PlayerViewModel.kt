package com.example.simpleplayertask6

import android.util.Log
import androidx.lifecycle.*
import com.example.exoplayermyui.data.TrackRepository
import com.example.exoplayermyui.data.TrackResource
import com.example.exoplayermyui.model.TrackJson
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val trackResource: TrackResource,
    private val application: PlayerApplication
    ): AndroidViewModel(application) {

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private var repository = TrackRepository(trackResource, PlayerApplication.context)
    private var _tracks = MutableLiveData<List<TrackJson>>()
    val tracks: LiveData<List<TrackJson>> get() = _tracks
    private var _player: SimpleExoPlayer? = null
    val player get() = requireNotNull(_player)
    private var currentMediaItem: Int = 0
    private var size = 0

    init {
        viewModelScope.launch {
            _tracks.value = repository.getTracks()
            Log.d("DEBUG", "Tracks ${tracks.value.toString()}")
        }
        size = if (tracks.value != null) {
            tracks.value!!.size
        } else {
            0
        }
    }

    fun playNext() {
        if (currentMediaItem < size - 1) {
            currentMediaItem++
        }
        Log.d("DEBUG", "PlayNext $currentMediaItem")
        player?.playWhenReady = false
        setMediaItem()?.let { player?.setMediaItem(it) }
        play()
    }

    fun playPrev() {
        if (currentMediaItem != 0) {
            currentMediaItem --
        }
        Log.d("DEBUG", "PlayPrev $currentMediaItem")
        player?.playWhenReady = false
        setMediaItem()?.let { player?.setMediaItem(it) }
        play()
    }

    fun play() {
        player?.playWhenReady = true
        player?.play()
    }

    fun stop() {
        player?.seekTo(0)
        player?.playWhenReady = false
    }

    fun pause() {
        player?.playWhenReady = false
    }

    fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        _player = null
    }

    fun initializePlayer() {
        _player = SimpleExoPlayer.Builder(PlayerApplication.context)
            .build()
            .also { exoPlayer ->
                setMediaItem()?.let { exoPlayer.setMediaItem(it) }
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()
            }
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun savePlayBackPosition() {
//        playbackPosition = player?.contentPosition ?: 0
//        //Log.d("DEBUG", position)
//    }

    private fun setMediaItem(): MediaItem? {
        val mediaItem = tracks.value?.get(currentMediaItem)?.let {
                trackJson ->  MediaItem.fromUri(trackJson.trackUri)
        }
        return mediaItem
    }

    fun getTrackTitle(): String {
        val title = tracks.value?.get(currentMediaItem)?.title

        return title ?: ""
    }

    fun getTrackAuthor(): String {
        val artistName = tracks.value?.get(currentMediaItem)?.artist
        return artistName ?: ""
    }

    fun getImageUri(): String {
        val imageUri = tracks.value?.get(currentMediaItem)?.bitmapUri
        return imageUri ?: ""
    }
}