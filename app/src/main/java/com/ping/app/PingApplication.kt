package com.ping.app

import android.app.Application
import com.ping.app.data.dao.PingMapRepo
import com.ping.app.data.repository.PingMapRepoImpl
import com.ping.app.ui.util.LocationHelper

class PingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocationHelper.initialize(this)
        pingMapRepo = PingMapRepoImpl.initialize(this)
    }
    
    companion object {
        lateinit var pingMapRepo: PingMapRepo
    }
}