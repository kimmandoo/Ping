package com.ping.app.ui.ui.feature.chat

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ping.app.R
import com.ping.app.databinding.FragmentChatBinding
import com.ping.app.ui.base.BaseFragment
import com.ping.app.ui.presentation.chat.ChatViewModel
import com.ping.app.ui.presentation.map.PingMapViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ChatFragment_싸피"
class ChatFragment : BaseFragment<FragmentChatBinding, ChatViewModel>(R.layout.fragment_chat) {
    override val viewModel: ChatViewModel by activityViewModels()
    private val pingMapViewModel: PingMapViewModel by activityViewModels()
    
    private val chatAdapter by lazy {
        ChatAdapter()
    }
    override fun initView(savedInstanceState: Bundle?) {

        if(viewModel.chatList.value?.size!! < 2) {
            lifecycleScope.launch {
                viewModel.initChatMsgSetting(pingMapViewModel.userLocation.value!!)
            }
        }

        binding.apply {
            chatFragRv.adapter = chatAdapter
            chatFragSend.setOnClickListener {
                viewModel.chatList(chatFragEd.text.toString(), 1)


                lifecycleScope.launch {
                    viewModel.callChatGpt(chatFragEd.text.toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.chatFragRv.scrollToPosition(viewModel.chatList.value?.size?.minus(1) ?: 0)
                    }
                }

                chatFragEd.setText("")
                binding.chatFragRv.scrollToPosition(viewModel.chatList.value?.size?.minus(1) ?: 0)

            }
        }

        viewModel.chatList.observe(viewLifecycleOwner){chatList ->
            chatAdapter.submitList(chatList)
        }
    }
}