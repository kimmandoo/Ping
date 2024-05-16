package com.ping.app.data.repository.main

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ping.app.data.model.Gathering
import kotlinx.coroutines.CompletableDeferred

private const val TAG = "MainRepoImpl_싸피"
class MainRepoImpl(context: Context): MainRepo {

    private val db = Firebase.firestore

    /**
     * 현재 사용자의 위치를 기준으로 모든 반경에서 1km 위치에 포함되는 모든 좌표들을 보여줘야함
     */
    override suspend fun getMeetingTable(lng:Double, lat:Double) : List<Gathering> {
        Log.d(TAG, "getMeetingTable: ")
        val meetingTable = db.collection("MEETING")
        lateinit var meetingTableResult: MutableList<Gathering>
        meetingTableResult = arrayListOf()

        val getMeeingTableDeffer = CompletableDeferred<QuerySnapshot>()

        val result = meetingTable


        result
            .get()
            .addOnSuccessListener {documents ->
                val meetingDefferResult = documents
                for(value in documents.documents){
                    val valuelat = value.data?.get("latitude").toString().toDouble()
                    val valuelng = value.data?.get("longitude").toString().toDouble()

                    if(lat-0.02 < valuelat && valuelat < lat + 0.02 && lng-0.02 < valuelng && valuelng < lng + 0.02 ) {
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
     * 모임 참가 버튼을 누르면 Meeting에 참가하는 로직입니다.
     *
     * 테스트 용도로 여기에 작성했으며 추후 이동 될 것입니다.
     */
    override fun participantsMeetingDetailTable(data: Gathering, userUid: String) {

        val meetingDetailTable = db.collection("DETAILMEETING")
        meetingDetailTable.document(data.uuid).update("Participants", FieldValue.arrayUnion(userUid))
    }

    /**
     * 모임 취소 버튼을 누르면 Meeting에 참가를 취소하는 로직입니다.
     *
     * 테스트 용도로 여기에 작성했으며 추후 이동 될 것입니다.
     */
    override fun cancellationOfParticipantsMeetingDetailTable(data: Gathering, userUid: String) {

        val meetingDetailTable = db.collection("DETAILMEETING")
        meetingDetailTable.document(data.uuid).update("Participants", FieldValue.arrayRemove(userUid))
    }


    companion object{
        private var INSTANCE : MainRepoImpl? = null

        fun initialize(context: Context) : MainRepoImpl{
            if(INSTANCE == null){
                synchronized(MainRepoImpl::class.java){
                    if(INSTANCE == null){
                        INSTANCE = MainRepoImpl(context)
                    }
                }
            }
            return INSTANCE!!
        }

        fun get():MainRepoImpl{
            return INSTANCE!!
        }
    }
}