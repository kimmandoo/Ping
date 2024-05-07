package com.ping.app.data.model

data class Gathering(
    val uid: String, // 모임 id
    val uuid: String, // 개최자 id
    val regTime: String,
    val content: String,
    val longitude: Double,
    val latitude: Double
)
