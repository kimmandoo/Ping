package com.ping.app.ui.presentation.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ping.app.data.model.gpt.ChatBubble
import com.ping.app.data.repository.map.PingMapRepoImpl

private const val TAG = "ChatViewModel_싸피"
class ChatViewModel : ViewModel() {
    private val mapInstance = PingMapRepoImpl.getInstance()
    private val _chatList = MutableLiveData<List<ChatBubble>>()
    val chatList : LiveData<List<ChatBubble>>
        get() = _chatList

    init {
        chatList("안녕하세요. :)",2)
    }

    fun chatList(msg:String, type : Int){
        val currentList = _chatList.value.orEmpty().toMutableList()
        currentList.add(ChatBubble(msg, type))
        _chatList.value = currentList
    }


}