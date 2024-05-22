package com.ping.app.data.repository.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.ping.app.data.model.Gathering
import com.ping.app.data.model.GatheringDetail
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

private const val TAG = "PingMapRepoImpl_싸피"

class PingMapRepoImpl private constructor(context: Context) : PingMapRepo {
    private val appContext: Context = context
    private val db = Firebase.firestore
    
    override fun sendPingInfo(data: Gathering) {
        Log.d(TAG, "sendPingInfo: $data")
        
        db.collection("MEETING")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                makeMeetingDetailTable(data)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
    
    
    override suspend fun requestAddress(lat: Double, lng: Double): String {
        val geoCoder = Geocoder(appContext, Locale.KOREA)
        val retrievedAddress = CompletableDeferred<String>()
        CoroutineScope(Dispatchers.IO).launch {
            if (Build.VERSION.SDK_INT >= 33) {
                runCatching {
                    geoCoder.getFromLocation(lat, lng, 1) {
                        val currentLocationAddress = it.firstOrNull()?.getAddressLine(0).toString()
                        retrievedAddress.complete(currentLocationAddress)
                    }
                }.onFailure {
                    Log.d(TAG, "requestAddress failed: $it")
                    retrievedAddress.complete("주소를 가져 올 수 없습니다.")
                }
            } else {
                runCatching {
                    @Suppress("DEPRECATION")
                    geoCoder.getFromLocation(lat, lng, 1) as ArrayList<Address>
                }.onSuccess { response ->
                    if (response.size > 0) {
                        val currentLocationAddress = response[0].getAddressLine(0)
                            .toString()
                        retrievedAddress.complete(currentLocationAddress)
                    }
                }.onFailure {
                    retrievedAddress.complete("주소를 가져 올 수 없습니다.")
                }
            }
        }
        
        return retrievedAddress.await()
    }
    
    /**
     * 해당 함수는 주최자가 meeting을 생성한 경우 그에 따른 DetailTable도 만들어 주는 함수입니다.
     */
    override fun makeMeetingDetailTable(data: Gathering) {
        
        db.collection("DETAILMEETING")
            .document(data.uuid)
            .set(GatheringDetail(10, data.content, arrayListOf(data.uid)))
            .addOnSuccessListener { documentReference ->
                FirebaseMessaging.getInstance().subscribeToTopic(data.uuid).addOnSuccessListener {
                    Log.d(TAG, "participantsMeetingDetailTable: success subscribed")
                }
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
    
    /**
     * 모임 참가 버튼을 누르면 Meeting에 참가하는 로직입니다.
     */
    override fun participantsMeetingDetailTable(data: Gathering, userUid: String) {
        Log.d(TAG, "participantsMeetingDetailTable: ${userUid}")
        val meetingDetailTable = db.collection("DETAILMEETING")
        FirebaseMessaging.getInstance().subscribeToTopic(data.uuid).addOnSuccessListener {
            Log.d(TAG, "participantsMeetingDetailTable: success subscribed")
        }
        meetingDetailTable.document(data.uuid)
            .update("participants", FieldValue.arrayUnion(userUid))
    }
    
    /**
     * 모임 취소 버튼을 누르면 Meeting에 참가를 취소하는 로직입니다.
     */
    override fun cancellationOfParticipantsMeetingDetailTable(data: Gathering, userUid: String) {
        
        if (data.uid == userUid) {
            organizercancellationOfParticipantsMeetingTable(data, userUid)
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(data.uuid).addOnSuccessListener {
                Log.d(TAG, "participantsMeetingDetailTable: success unsubscribed")
            }
            val meetingDetailTable = db.collection("DETAILMEETING")
            meetingDetailTable.document(data.uuid)
                .update("participants", FieldValue.arrayRemove(userUid))
        }
    }
    
    override fun organizercancellationOfParticipantsMeetingTable(data: Gathering, userUid: String) {
        db.collection("DETAILMEETING")
            .document(data.uuid)
            .delete()
        
        db.collection("MEETING")
            .whereEqualTo("uid", userUid)
            .get()
            .addOnSuccessListener {
                db.collection("MEETING")
                    .document(it.documents.get(0).id)
                    .delete()
            }
    }
    
    
    override suspend fun getUserName(userUid: String): String {
        var queryResultName = CompletableDeferred<String>()
        val userTable = db.collection("USER")
        
        userTable.document(userUid)
            .get()
            .addOnSuccessListener {
                queryResultName.complete(it.data?.get("name").toString())
            }
        
        return queryResultName.await()
    }
    
    
    companion object {
        private var instance: PingMapRepoImpl? = null
        fun initialize(context: Context): PingMapRepoImpl {
            if (instance == null) {
                synchronized(PingMapRepoImpl::class.java) {
                    if (instance == null) {
                        instance = PingMapRepoImpl(context)
                    }
                }
            }
            return instance!!
        }
        
        fun getInstance(): PingMapRepoImpl {
            return instance!!
        }
    }
}