package com.ping.app.data.remote

import com.ping.app.data.model.gpt.ChatGptRequest
import com.ping.app.data.model.gpt.ChatGptResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGPTService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(@Body request: ChatGptRequest): Response<ChatGptResponse>
}