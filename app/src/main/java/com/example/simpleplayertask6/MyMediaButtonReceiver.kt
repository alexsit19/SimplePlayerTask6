package com.example.simpleplayertask6

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.media.session.MediaButtonReceiver

class MyMediaButtonReceiver : MediaButtonReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            super.onReceive(context, intent)
        } catch (e: IllegalStateException) {
            Log.d("DEBUG", "EXCEPTION ${e.message}")
        }
    }
}
