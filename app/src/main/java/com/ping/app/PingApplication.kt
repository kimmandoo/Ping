package com.ping.app

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ping.app.data.repository.chatgpt.ChatGPTRepo
import com.ping.app.data.repository.chatgpt.ChatGPTRepoImpl
import com.ping.app.data.repository.login.LoginRepo
import com.ping.app.data.repository.login.LoginRepoImpl
import com.ping.app.data.repository.main.MainRepo
import com.ping.app.data.repository.main.MainRepoImpl
import com.ping.app.data.repository.map.PingMapRepo
import com.ping.app.data.repository.map.PingMapRepoImpl
import com.ping.app.ui.ui.container.MainActivity
import com.ping.app.ui.ui.util.LocationHelper

val Context.tokenDataStore by preferencesDataStore(name = "uid")

class PingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        locationHelper = LocationHelper.initialize(this)
        pingMapRepo = PingMapRepoImpl.initialize(this)
        mainRepo = MainRepoImpl.initialize(this)
        gptRepo = ChatGPTRepoImpl.initialize()
    }
    
    companion object {
        val UID_TOKEN = stringPreferencesKey("uid")
        lateinit var gptRepo: ChatGPTRepo
        lateinit var locationHelper: LocationHelper
        lateinit var pingMapRepo: PingMapRepo
        lateinit var loginRepo: LoginRepo
        lateinit var mainRepo: MainRepo
    }
}