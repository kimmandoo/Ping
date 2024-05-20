package com.ping.app.data.model.gpt

import com.google.gson.annotations.SerializedName

data class ChatGptResponse(
    @SerializedName("choices") val choices: List<Choice>
)