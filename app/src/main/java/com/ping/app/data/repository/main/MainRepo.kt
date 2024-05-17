package com.ping.app.data.repository.main

import com.ping.app.data.model.Gathering

interface MainRepo {
    suspend fun getMeetingTable(lng:Double, lat:Double) : List<Gathering>

    suspend fun meetingsToAttend(userUid: String) : Gathering
}