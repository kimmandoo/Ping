package com.ping.app.data.repository.map

import com.ping.app.data.model.Gathering

interface PingMapRepo {
    fun sendPingInfo(data: Gathering)
    suspend fun requestAddress(lat: Double, lng: Double): String

    fun makeMeetingDetailTable(data: Gathering)

    fun participantsMeetingDetailTable(data: Gathering, userUid:String)

    fun cancellationOfParticipantsMeetingDetailTable(data: Gathering, userUid: String)

    fun organizercancellationOfParticipantsMeetingTable(data: Gathering, userUid: String)

    suspend fun getUserName(userUid: String) : String
}