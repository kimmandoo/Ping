package com.ping.app.data.repository.map

import com.ping.app.data.model.Gathering

interface PingMapRepo {
    fun sendPingInfo(data: Gathering)
    fun requestAddress(lat: Double, lng: Double): String

    fun makeMeetingDetailTable(data: Gathering)
}