package com.ping.app

import android.app.Application
import com.ping.app.data.repository.map.PingMapRepoImpl
import com.ping.app.data.repository.login.LoginRepoImpl
import com.ping.app.data.repository.login.LoginRepo
import com.ping.app.data.repository.map.PingMapRepo
import com.ping.app.ui.ui.util.LocationHelper

class PingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        locationHelper = LocationHelper.initialize(this)
        pingMapRepo = PingMapRepoImpl.initialize(this)
        loginRepo = LoginRepoImpl.initialize(this)
    }

    companion object {
        lateinit var locationHelper: LocationHelper
        lateinit var pingMapRepo: PingMapRepo
        lateinit var loginRepo: LoginRepo
    }
}