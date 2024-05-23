package com.ping.app.ui.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.ping.app.data.model.gpt.ChatBubble
import com.ping.app.data.model.gpt.Message
import com.ping.app.data.repository.chatgpt.ChatGPTRepoImpl
import com.ping.app.data.repository.map.PingMapRepoImpl
import com.ping.app.ui.ui.util.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ChatViewModel_싸피"

class ChatViewModel : ViewModel() {
    private val mapInstance = PingMapRepoImpl.getInstance()
    private val _chatList = MutableStateFlow<List<ChatBubble>>(listOf())
    val chatList: StateFlow<List<ChatBubble>> get() = _chatList.asStateFlow()
    
    init {
        chatList("안녕하세요. :)", 2)
    }
    
    suspend fun initChatMsgSetting(latLng: LatLng) {
        val msg =
            "${mapInstance.requestAddress(latLng.latitude, latLng.longitude)} 이 주소에서 추천하는 장소가 있어?"
        callChatGpt(msg)
    }
    
    fun chatList(msg: String, type: Int) {
        viewModelScope.launch {
            val currentList = _chatList.value.toMutableList()
            currentList.add(ChatBubble(msg, type))
            _chatList.emit(currentList)
        }
    }
    
    fun clearGpt() {
        viewModelScope.launch {
            val currentList = mutableListOf<ChatBubble>()
            currentList.add(ChatBubble("안녕하세요. :)", 2))
            _chatList.emit(currentList)
        }
    }
    
    suspend fun callChatGpt(msg: String) {
        viewModelScope.launch {
            val messages = listOf(
                Message(role = "system", content = "너는 매우 친절한 가이드야"),
                Message(role = "system", content = "모든 답변은 200글자 이내로 해줘" + "그리고 말투는 ~습니다. 로 마무리해"),
                Message(role = "user", content = msg)
            )
            runCatching {
                ChatGPTRepoImpl.getInstance()
                    .getChatCompletion(messages).choices.first().message.content
            }.onSuccess {
                chatList(it, 2)
            }.onFailure {
                chatList("오류가 발생했습니다. 잠시후 다시 시작해주세요", 2)
            }
        }
    }
}