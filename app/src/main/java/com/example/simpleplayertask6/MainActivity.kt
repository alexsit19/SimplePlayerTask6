package com.example.simpleplayertask6

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.simpleplayertask6.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val playerViewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory()
    }

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        updateUi()

        viewBinding.playBtn.setOnClickListener {
            playerViewModel.play()
            updateUi()
        }
        viewBinding.pauseBtn.setOnClickListener {
            playerViewModel.pause()
            updateUi()
        }
        viewBinding.stopBtn.setOnClickListener {
            playerViewModel.stop()
            updateUi()
        }
        viewBinding.nextBtn.setOnClickListener {
            playerViewModel.playNext()
            updateUi()
        }
        viewBinding.prevBtn.setOnClickListener {
            playerViewModel.playPrev()
            updateUi()
        }
    }

    private fun updateUi() {
        with(viewBinding) {
            trackImage.load(playerViewModel.getImageUri())
            titleText.text = playerViewModel.getTrackTitle()
            artistText.text = playerViewModel.getTrackAuthor()
            playBtn.isEnabled = playerViewModel.getPlayButtonStatus()
            pauseBtn.isEnabled = playerViewModel.getPauseButtonStatus()
            stopBtn.isEnabled = playerViewModel.getStopButtonStatus()
        }
    }
}
