package com.ping.app.data.model.gpt

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)