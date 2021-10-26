package com.example.simpleplayertask6

import android.app.Application
import android.content.Context
import android.util.Log

class PlayerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("DEBUG", "Application create")
        context = applicationContext
    }

    companion object {
        lateinit var context: Context

    }

}