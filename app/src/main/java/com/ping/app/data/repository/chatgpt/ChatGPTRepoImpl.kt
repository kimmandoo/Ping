package com.ping.app.data.repository.chatgpt

import com.ping.app.data.model.gpt.ChatGptRequest
import com.ping.app.data.model.gpt.ChatGptResponse
import com.ping.app.data.model.gpt.Message
import com.ping.app.data.remote.NetworkModule
import kotlinx.coroutines.CompletableDeferred

private const val TAG = "ChatGPTRepoImpl_싸피"
class ChatGPTRepoImpl: ChatGPTRepo {
    private val api = NetworkModule.api
    
    override suspend fun getChatCompletion(messages: List<Message>): ChatGptResponse {
        val request = ChatGptRequest(
            model = "gpt-4o",
            messages = messages
        )
        val gptAnswer = CompletableDeferred<ChatGptResponse>()
        runCatching { api.getChatCompletion(request) }.onSuccess {
            it.body()?.let {
                gptAnswer.complete(it)
            }
            it.errorBody()?.let {
                gptAnswer.completeExceptionally(Throwable("잘못된 값으로 요청했습니다."))
            }
        }
        return gptAnswer.await()
    }
    
    companion object {
        private var instance: ChatGPTRepoImpl? = null
        fun initialize(): ChatGPTRepoImpl {
            if (instance == null) {
                synchronized(ChatGPTRepoImpl::class.java) {
                    if (instance == null) {
                        instance = ChatGPTRepoImpl()
                    }
                }
            }
            return instance!!
        }
        
        fun getInstance(): ChatGPTRepoImpl {
            return instance!!
        }
    }
}