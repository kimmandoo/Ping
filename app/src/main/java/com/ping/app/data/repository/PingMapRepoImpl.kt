package com.ping.app.data.repository

import android.content.Context
import com.ping.app.data.dao.PingMapRepo
import com.ping.app.data.model.Gathering

class PingMapRepoImpl private constructor(context: Context): PingMapRepo {
    override fun sendPingInfo(data: Gathering) {

    }
    
    companion object {
        private var instance: PingMapRepoImpl? = null
        fun initialize(context: Context): PingMapRepoImpl {
            if (instance == null) {
                synchronized(PingMapRepoImpl::class.java) {
                    if (instance == null) {
                        instance = PingMapRepoImpl(context)
                    }
                }
            }
            return instance!!
        }
        
        fun getInstance(): PingMapRepoImpl {
            return instance!!
        }
    }
}