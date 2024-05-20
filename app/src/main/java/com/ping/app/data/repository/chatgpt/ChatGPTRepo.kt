package com.ping.app.data.repository.chatgpt

import com.ping.app.data.model.gpt.ChatGptResponse
import com.ping.app.data.model.gpt.Message

interface ChatGPTRepo {
    suspend fun getChatCompletion(messages: List<Message>): ChatGptResponse
}