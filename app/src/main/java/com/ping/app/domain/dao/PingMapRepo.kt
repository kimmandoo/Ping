package com.ping.app.domain.dao

import com.ping.app.domain.model.Gathering

interface PingMapRepo {
    fun sendPingInfo(data: Gathering)
    fun requestAddress(lat: Double, lng: Double): String
}