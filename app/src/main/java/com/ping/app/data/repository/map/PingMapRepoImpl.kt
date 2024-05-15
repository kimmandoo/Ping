package com.ping.app.data.repository.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ping.app.data.model.Gathering
import com.ping.app.data.model.GatheringDetail
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


    
    override fun requestAddress(lat: Double, lng: Double): String {
        val geoCoder = Geocoder(appContext, Locale.KOREA)
        var addressResult = "주소를 가져 올 수 없습니다."
        runCatching {
            geoCoder.getFromLocation(lat, lng, 1) as ArrayList<Address>
        }.onSuccess { response ->
            if (response.size > 0) {
                val currentLocationAddress = response[0].getAddressLine(0)
                    .toString()
                addressResult = currentLocationAddress
            }
        }.onFailure {
            Log.d("requestAddress", "error: ${it.stackTrace}")
        }
        
        return addressResult
    }

    /**
     * 해당 함수는 주최자가 meeting을 생성한 경우 그에 따른 DetailTable도 만들어 주는 함수입니다.
     */
    override fun makeMeetingDetailTable(data: Gathering) {

        db.collection("DETAILMEETING")
            .document(data.uuid)
            .set(GatheringDetail(10, arrayListOf(data.uid)))
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
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