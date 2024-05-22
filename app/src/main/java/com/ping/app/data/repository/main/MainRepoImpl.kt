package com.ping.app.data.repository.main

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ping.app.data.model.Gathering
import kotlinx.coroutines.CompletableDeferred

private const val TAG = "MainRepoImpl_싸피"

class MainRepoImpl(context: Context) : MainRepo {

    private val db = Firebase.firestore

    /**
     * 현재 사용자의 위치를 기준으로 모든 반경에서 1km 위치에 포함되는 모든 좌표들을 보여줘야함
     */
    override suspend fun getMeetingTable(lng: Double, lat: Double): List<Gathering> {
        val meetingTable = db.collection("MEETING")
        val getMeetingTableDeffer = CompletableDeferred<List<Gathering>>()
        meetingTable
            .get()
            .addOnSuccessListener { documents ->
                val meetingTableResult: MutableList<Gathering> = mutableListOf()
                for (value in documents.documents) {
                    val valuelat = value.data?.get("latitude").toString().toDouble()
                    val valuelng = value.data?.get("longitude").toString().toDouble()
                    if (lat - 0.02 < valuelat && valuelat < lat + 0.02 && lng - 0.02 < valuelng && valuelng < lng + 0.02) {
                        meetingTableResult.add(
                            Gathering(
                                uid = value.data?.get("uid").toString(),
                                uuid = value.data?.get("uuid").toString(),
                                enterCode = value.data?.get("enterCode").toString(),
                                gatheringTime = value.data?.get("gatheringTime").toString(),
                                content = value.data?.get("content").toString(),
                                title = value.data?.get("title").toString(),
                                latitude = valuelat,
                                longitude = valuelng,
                                organizer = value.data?.get("organizer").toString(),
                            )
                        )
                    }
                }
                getMeetingTableDeffer.complete(meetingTableResult)
            }
        return getMeetingTableDeffer.await()
    }

    /**
     * 해당 로직은 userUid를 통해 DetailMeeting 테이블에 접근하여 해당 userUid가 포함된 DetailMeeting Table의 id를 가져온 후
     * 해당 id를 통해 Meeting Table에 해당 id가 포함된 정보를 가져오는 로직입니다.
     */
    override suspend fun meetingsToAttend(userUid: String): Gathering? {
        val joinedGathering = CompletableDeferred<Gathering>()
        val detailMeetingTable = db.collection("DETAILMEETING")

        detailMeetingTable
            // 일단 내가 생성한 핑까지 다 가져온다
            .whereArrayContains("participants", userUid)
            .get()
            .addOnSuccessListener { resultDetailMeetingTable ->
                // detailMeeting 테이블에서 유저 uid를 가지고 있는 것들만 가져옴
                for (doc in resultDetailMeetingTable.documents) {
                    if ((doc["participants"] as List<String>).first() == userUid) {
                        continue
                    }
                    val meetingTable = db.collection("MEETING")
                    meetingTable
                        .whereEqualTo("uuid", doc.id)
                        .get()
                        .addOnSuccessListener { resultMeetingTable ->
                            // 내가 생성한 핑이 아닌데 참여한 게 있을 경우
                            for (meeting in resultMeetingTable.documents) {
                                meeting.data?.let {
                                    val isValid = it["gatheringTime"].toString()
                                        .toLong() > System.currentTimeMillis()
                                    if (isValid) {
                                        Log.d(TAG, "meetingsToAttend: $it")
                                        joinedGathering.complete(
                                            Gathering(
                                                it["uid"].toString(),
                                                it["uuid"].toString(),
                                                it["organizer"].toString(),
                                                it["enterCode"].toString(),
                                                it["gatheringTime"].toString(),
                                                it["title"].toString(),
                                                it["content"].toString(),
                                                it["longitude"].toString().toDouble(),
                                                it["latitude"].toString().toDouble(),
                                            )
                                        )
                                    }
                                }
                            }
                        }
                }
            }
        val result = joinedGathering.await()
        return if (result.gatheringTime.toLong() > System.currentTimeMillis()) {
            result
        } else {
            null
        }
    }

    override suspend fun detailMeetingDuplicateCheck(
        gathering: Gathering,
        userUid: String
    ): Boolean {
        val duplicateResult = CompletableDeferred<Boolean>()
        val detailMeetingTable = db.collection("DETAILMEETING").document(gathering.uuid)
        detailMeetingTable
            .get()
            .addOnSuccessListener { resultDetailMeetingTable ->
                val data = resultDetailMeetingTable.data?.get("participants") as? List<String>
                if (data != null) {
                    duplicateResult.complete(data.contains(userUid))
                }
            }
        return duplicateResult.await()
    }

    override suspend fun meetingDuplicateCheck(userUid: String): Boolean {
        val duplicateResult = CompletableDeferred<Boolean>()
        val meetingTable = db.collection("MEETING").whereEqualTo("uid", userUid)
        val currentTime = System.currentTimeMillis()

        meetingTable
            .get()
            .addOnSuccessListener { resultMeetingTable ->
                for (resultMeetingElement in resultMeetingTable.documents) {
                    if (resultMeetingElement.data?.get("gatheringTime").toString()
                            .toLong() > currentTime
                    ) {
                        duplicateResult.complete(false)
                    }
                }
            }
            .addOnCompleteListener {
                duplicateResult.complete(true)
            }
        return duplicateResult.await()
    }

    override suspend fun organizerMeetingTableCheck(userUid: String): Gathering? {
        val meetingTable = db.collection("MEETING")
        val waitingOrganizerMeeting = CompletableDeferred<Gathering>()

//        delay(1000)

        meetingTable
            .whereEqualTo("uid", userUid)
            .get()
            .addOnSuccessListener { organizerGathering ->
                for (organizerGatheringData in organizerGathering) {
                    val nowTime = System.currentTimeMillis()
                    if (organizerGatheringData.data["gatheringTime"].toString()
                            .toLong() > nowTime
                    ) {
                        waitingOrganizerMeeting.complete(
                            Gathering(
                                organizerGatheringData.data["uid"].toString(),
                                organizerGatheringData.data["uuid"].toString(),
                                organizerGatheringData.data["organizer"].toString(),
                                organizerGatheringData.data["enterCode"].toString(),
                                organizerGatheringData.data["gatheringTime"].toString(),
                                organizerGatheringData.data["title"].toString(),
                                organizerGatheringData.data["content"].toString(),
                                organizerGatheringData.data["longitude"].toString().toDouble(),
                                organizerGatheringData.data["latitude"].toString().toDouble()
                            )
                        )
                    }
                }
            }

        val result = waitingOrganizerMeeting.await()
        Log.d(TAG, "organizerMeetingTableCheck@@@@@@@@@@@@: ${result}")
        return if (result != null){
            result
        }else{
            null
        }

    }

    suspend fun getAllUserName(): HashMap<String, String> {
        val userNameList = CompletableDeferred<HashMap<String, String>>()
        val userTable = db.collection("USER")
        userTable.get().addOnSuccessListener { task ->
            val map = hashMapOf<String, String>()
            for (user in task.documents) {
                map[user.id] = user.data?.get("name").toString()
            }
            userNameList.complete(map)
        }
        return userNameList.await()
    }


    companion object {
        private var INSTANCE: MainRepoImpl? = null

        fun initialize(context: Context): MainRepoImpl {
            if (INSTANCE == null) {
                synchronized(MainRepoImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MainRepoImpl(context)
                    }
                }
            }
            return INSTANCE!!
        }

        fun get(): MainRepoImpl {
            return INSTANCE!!
        }
    }
}