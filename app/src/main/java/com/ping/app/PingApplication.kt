package com.ping.app

import android.app.Application
import com.ping.app.presentation.util.LocationHelper

class PingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocationHelper.getInstance(this)
    }
}