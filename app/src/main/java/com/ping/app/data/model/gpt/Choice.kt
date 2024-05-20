package com.ping.app.data.model.gpt

import com.google.gson.annotations.SerializedName

data class Choice(
    @SerializedName("message") val message: Message
)