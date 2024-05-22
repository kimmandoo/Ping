package com.ping.app.data.repository.main

import com.ping.app.data.model.Gathering

interface MainRepo {
    suspend fun getMeetingTable(lng:Double, lat:Double) : List<Gathering>

    suspend fun meetingsToAttend(userUid: String) : Gathering?

    suspend fun detailMeetingDuplicateCheck(gathering: Gathering, userUid: String):Boolean

    suspend fun meetingDuplicateCheck(userUid: String):Boolean

    suspend fun organizerMeetingTableCheck(userUid: String): Gathering?

}