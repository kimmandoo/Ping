package com.ping.app.data.model

data class Gathering(
    val uid: String, // 모임 id
    val uuid: String, // 개최자 id
    val enterCode: String,
    val gatheringTime: String,
    val title: String,
    val content: String,
    val longitude: Double,
    val latitude: Double
)
