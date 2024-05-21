package com.ping.app.ui.ui.util

import com.naver.maps.geometry.LatLng

object Map{
    const val GPS_ENABLE_REQUEST_CODE = 2001
    const val UPDATE_INTERVAL = 1000
    const val FASTEST_UPDATE_INTERVAL = 500
    const val MAP_BOUNDS = 1000.0
    const val USER_POSITION_LAT = "LAT"
    const val USER_POSITION_LNG = "LNG"
}

object FCM{
    const val CHANNEL_NAME= "ping"
    const val CHANNEL_ID = "ping"
    const val NOTIFICATION_ID = 1001
}

val starbucks = LatLng(36.107953, 128.418385)
val CLICK_DELAY = 1000L
enum class userTableColunm{
    NAME, EMAIL, REGION, MeetingManagerUID
}