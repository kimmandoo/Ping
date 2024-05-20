package com.ping.app.data.repository.main

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
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
        lateinit var meetingTableResult: MutableList<Gathering>
        meetingTableResult = arrayListOf()

        val getMeeingTableDeffer = CompletableDeferred<QuerySnapshot>()

        val result = meetingTable


        result
            .get()
            .addOnSuccessListener { documents ->
                val meetingDefferResult = documents
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
                                latitude = value.data?.get("latitude").toString().toDouble(),
                                longitude = value.data?.get("longitude").toString().toDouble()
                            )
                        )
                    }
                }

                getMeeingTableDeffer.complete(meetingDefferResult)

            }

        getMeeingTableDeffer.await()

        return meetingTableResult

    }

    /**
     * 해당 로직은 userUid를 통해 DetailMeeting 테이블에 접근하여 해당 userUid가 포함된 DetailMeeting Table의 id를 가져온 후
     * 해당 id를 통해 Meeting Table에 해당 id가 포함된 정보를 가져오는 로직입니다.
     */
    override suspend fun meetingsToAttend(userUid: String): Gathering {

        val meetingsToAttendTable = CompletableDeferred<QuerySnapshot>()
        var meetingsToAttendResult = Gathering("", "", "", "", "","", 0.0, 0.0)
        var resultDetailMeetingDocument = ""
        
        val detailMeetingTable = db.collection("DETAILMEETING")
        detailMeetingTable
            .get()
            .addOnSuccessListener { resultDetailMeetingTable ->
                // detailMeeting 테이블에서 유저 uid를 가지고 있는 데이터를 가져옴
                for (detailMeetingDocument in resultDetailMeetingTable) {

                    val data = detailMeetingDocument.data["participants"] as? List<String>
                    if (data != null) {
                        for (i in 1..<data.size) {
                            if (userUid == data.get(i).toString()) {
                                // 해당 Meeting 테이블로 접근하여 해당 uuid에 맞는 테이블을 가져옴
                                val meetingTable = db.collection("MEETING")

                                meetingTable
                                    .get()
                                    .addOnSuccessListener { resultMeetingTable ->
                                        resultDetailMeetingDocument = detailMeetingDocument.id
                                        meetingsToAttendTable.complete(
                                            resultMeetingTable
                                        )
                                    }

                            }
                        }
                    } else {
//                        Log.d(TAG, "No participants found")
                    }

                }
            }
        val resultMeetingTable = meetingsToAttendTable.await()

        for (meetingDocument in resultMeetingTable) {
            val dataUUID =
                meetingDocument.data["uuid"] as? String
            if (resultDetailMeetingDocument == dataUUID) {
                meetingsToAttendResult = Gathering(
                    meetingDocument.data["uid"].toString(),
                    meetingDocument.data["uuid"].toString(),
                    meetingDocument.data["enterCode"].toString(),
                    meetingDocument.data["gatheringTime"].toString(),
                    meetingDocument.data["title"].toString(),
                    meetingDocument.data["content"].toString(),
                    meetingDocument.data["longitude"].toString().toDouble(),
                    meetingDocument.data["latitude"].toString().toDouble(),
                )
                break
            }
        }

        Log.d(TAG, "meetingsToAttend: ${meetingsToAttendResult}")

        return meetingsToAttendResult
    }

    override suspend fun detailMeetingDuplicateCheck(gathering: Gathering, userUid: String): Boolean {
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
            .addOnSuccessListener {resultMeetingTable ->
                for(resultMeetingElement in resultMeetingTable.documents){
                    if(resultMeetingElement.data?.get("gatheringTime").toString().toLong() > currentTime){
                        duplicateResult.complete(false)
                    }
                }
            }
            .addOnCompleteListener {
                duplicateResult.complete(true)
            }
        return duplicateResult.await()
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