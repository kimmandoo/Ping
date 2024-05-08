package com.ping.app.data.dao

import com.ping.app.data.model.Gathering

interface PingMapRepo {
    fun sendPingInfo(data: Gathering)
}