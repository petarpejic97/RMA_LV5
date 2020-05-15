package com.example.soundboard

import android.app.Application
import android.content.Context

class soundboard : Application() {
    companion object {
        lateinit var ApplicationContext: Context
            private set
    }
    override fun onCreate() {
        super.onCreate()
        ApplicationContext = this
    }

}