package com.ping.app.data.model.gpt

import com.google.gson.annotations.SerializedName

data class ChatGptRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<Message>
)

