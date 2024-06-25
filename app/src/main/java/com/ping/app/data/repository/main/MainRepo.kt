package com.ping.app.data.repository.main

import com.ping.app.data.model.Gathering

interface MainRepo {
    suspend fun getMeetingTableWithPosition(lng:Double, lat:Double) : List<Gathering>

    suspend fun getMeetingsToAttend(userUid: String) : Gathering?

    suspend fun checkDetailMeetingDuplicate(gathering: Gathering, userUid: String):Boolean

    suspend fun checkMeetingDuplicate(userUid: String):Boolean

    suspend fun checkOrganizerMeetingTable(userUid: String): Gathering?

}