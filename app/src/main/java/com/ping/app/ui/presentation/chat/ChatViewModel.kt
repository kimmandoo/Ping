package com.ping.app.ui.presentation.chat

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ping.app.data.model.gpt.ChatBubble
import com.ping.app.data.model.gpt.ChatGptResponse
import com.ping.app.data.model.gpt.Message
import com.ping.app.data.repository.chatgpt.ChatGPTRepoImpl
import com.ping.app.data.repository.map.PingMapRepoImpl
import com.ping.app.ui.ui.util.LocationHelper
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        val locationDeffer = CompletableDeferred<Location?>()
        CoroutineScope(Dispatchers.IO).launch {
            locationDeffer.complete(locationHelperInstance.currentLocation)
            CoroutineScope(Dispatchers.Main).launch {
                val test  = initChat("${mapInstance.requestAddress(locationHelperInstance.currentLocation?.latitude.toString().toDouble(),  locationHelperInstance.currentLocation?.longitude.toString().toDouble())} 이 주소에서 추천하는 장소가 있어?")
                chatList(test, 2)
            }
        }
    }

    private suspend fun initChat(msg:String):String{
        val test = CompletableDeferred<ChatGptResponse>()
        viewModelScope.launch {
            val messages = listOf(
                Message(role = "user", content = msg)
            )
            test.complete(ChatGPTRepoImpl.getInstance().getChatCompletion(messages))
        }
        return test.await().choices.toList().get(0).message.content
    }

    fun chatList(msg:String, type : Int){
        val currentList = _chatList.value.orEmpty().toMutableList()
        currentList.add(ChatBubble(msg, type))
        _chatList.value = currentList
    }


}