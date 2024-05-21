package com.ping.app.ui.ui.feature.chat

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ping.app.R
import com.ping.app.data.model.gpt.ChatGptResponse
import com.ping.app.data.model.gpt.Message
import com.ping.app.data.repository.chatgpt.ChatGPTRepoImpl
import com.ping.app.databinding.FragmentChatBinding
import com.ping.app.ui.base.BaseFragment
import com.ping.app.ui.presentation.chat.ChatViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ChatFragment_싸피"
class ChatFragment : BaseFragment<FragmentChatBinding, ChatViewModel>(R.layout.fragment_chat) {
    override val viewModel: ChatViewModel by viewModels()

    private val chatAdapter by lazy {
        ChatAdapter()
    }
    override fun initView(savedInstanceState: Bundle?) {


        binding.apply {
            chatFragRv.adapter = chatAdapter
            chatFragSend.setOnClickListener {
                viewModel.chatList(chatFragEd.text.toString(), 1)
                binding.chatFragRv.scrollToPosition(viewModel.chatList.value?.size?.minus(1) ?: 0)

                lifecycleScope.launch {
                    callChatGpt(chatFragEd.text.toString())
                }

                chatFragEd.setText("")

            }
        }

        viewModel.chatList.observe(viewLifecycleOwner){chatList ->
            chatAdapter.submitList(chatList)
        }
    }

    private suspend fun callChatGpt(msg:String){
        val test = CompletableDeferred<ChatGptResponse>()
        lifecycleScope.launch {
            val messages = listOf(
                Message(role = "user", content = msg)
            )
            test.complete(ChatGPTRepoImpl.getInstance().getChatCompletion(messages))
        }

        viewModel.chatList(test.await().choices.toList().get(0).message.content, 2)
        CoroutineScope(Dispatchers.Main).launch {
            binding.chatFragRv.scrollToPosition(viewModel.chatList.value?.size?.minus(1) ?: 0)
        }
    }
}