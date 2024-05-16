package com.ping.app.ui.ui.util

import android.content.Context
import android.widget.Toast
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import kotlin.math.pow
import kotlin.math.roundToInt

fun round(number: Double, scale: Int): Double {
    val factor = 10.0.pow(scale.toDouble())
    return (number * factor).roundToInt() / factor
}

fun Context.easyToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun NaverMap.init(){
    this.apply {
        minZoom = 14.0
        maxZoom = 18.0
        cameraPosition = CameraPosition(
            LatLng(
                this.cameraPosition.target.latitude,
                this.cameraPosition.target.longitude
            ), 16.0
        )
        uiSettings.apply {
            isZoomControlEnabled = false
        }
    }
}

fun NaverMap.withMarker(marker: Marker){
    this.addOnCameraChangeListener { type, animated ->
        marker.position = LatLng(
            // 현재 보이는 네이버맵의 정중앙 가운데로 마커 이동
            this@withMarker.cameraPosition.target.latitude,
            this@withMarker.cameraPosition.target.longitude
        )
    }
    
    // 카메라의 움직임 종료에 대한 이벤트 리스너 인터페이스.
    this.addOnCameraIdleListener {
        marker.apply {
            position = LatLng(
                this@withMarker.cameraPosition.target.latitude,
                this@withMarker.cameraPosition.target.longitude
            )
        }
    }
}