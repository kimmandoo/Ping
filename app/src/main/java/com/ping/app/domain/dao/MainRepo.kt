package com.ping.app.domain.dao

import com.ping.app.domain.model.Gathering

interface MainRepo {
    suspend fun getMeetingTable(lng:Double, lat:Double) : List<Gathering>
}