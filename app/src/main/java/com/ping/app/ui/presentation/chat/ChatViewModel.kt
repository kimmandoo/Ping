package com.ping.app.ui.presentation.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.ping.app.data.model.gpt.ChatBubble
import com.ping.app.data.model.gpt.ChatGptResponse
import com.ping.app.data.model.gpt.Message
import com.ping.app.data.repository.chatgpt.ChatGPTRepoImpl
import com.ping.app.data.repository.map.PingMapRepoImpl
import com.ping.app.ui.ui.util.LocationHelper
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch

private const val TAG = "ChatViewModel_싸피"
class ChatViewModel : ViewModel() {
    private val mapInstance = PingMapRepoImpl.getInstance()
    private val locationHelperInstance = LocationHelper.getInstance()
    private val _chatList = MutableLiveData<List<ChatBubble>>()
    val chatList : LiveData<List<ChatBubble>>
        get() = _chatList

    init {
        chatList("안녕하세요. :)",2)
    }

    suspend fun initChatMsgSetting(latLng: LatLng){
        val msg =  "${mapInstance.requestAddress(latLng.latitude, latLng.longitude)} 이 주소에서 추천하는 장소가 있어?"
        callChatGpt(msg)
    }

    fun chatList(msg:String, type : Int){
        val currentList = _chatList.value.orEmpty().toMutableList()
        currentList.add(ChatBubble(msg, type))
        _chatList.value = currentList
    }

    suspend fun callChatGpt(msg:String){
        val test = CompletableDeferred<ChatGptResponse>()
        viewModelScope.launch {
            val messages = listOf(
                Message(role = "user", content = msg + "모든 답변은 200글자 이내로 해줘")
            )
            test.complete(ChatGPTRepoImpl.getInstance().getChatCompletion(messages))
        }
        chatList(test.await().choices.toList().get(0).message.content, 2)
    }
}