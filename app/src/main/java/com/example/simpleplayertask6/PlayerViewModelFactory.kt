package com.example.simpleplayertask6

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.exoplayermyui.data.TrackResource

class PlayerViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayerViewModel(TrackResource(), PlayerApplication()) as T
    }
}
