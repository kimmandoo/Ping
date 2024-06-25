package com.ping.app.ui.ui.feature.chat

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ping.app.R
import com.ping.app.databinding.FragmentChatBinding
import com.ping.app.ui.base.BaseBottomSheetDialogFragment
import com.ping.app.ui.presentation.chat.ChatViewModel
import com.ping.app.ui.presentation.map.PingMapViewModel
import com.ping.app.ui.ui.util.doOnTrue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "ChatFragment_싸피"

class ChatFragment :
    BaseBottomSheetDialogFragment<FragmentChatBinding, ChatViewModel>(R.layout.fragment_chat) {
    override val viewModel: ChatViewModel by activityViewModels()
    private val pingMapViewModel: PingMapViewModel by activityViewModels()
    
    private val chatAdapter by lazy {
        ChatAdapter()
    }
    
    override fun initView(savedInstanceState: Bundle?) {
        
        binding.apply {
            chatFragRv.apply {
                adapter = chatAdapter
            }
            
            lifecycleScope.launch {
                viewModel.chatList("주변에서 뭐 할지 추천해줘", 1)
                viewModel.initChatMsgSetting(pingMapViewModel.userLocation.value!!)
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.chatList.collectLatest {
                        binding.chatFragRv.smoothScrollToPosition((it.size - 1) ?: 0)
                        chatAdapter.submitList(it)
                    }
                }
            }
            
            chatFragSend.setOnClickListener {
                chatFragEd.text.isNotEmpty().doOnTrue {
                    viewModel.chatList(chatFragEd.text.toString(), 1)
                    lifecycleScope.launch {
                        viewModel.callChatGpt(chatFragEd.text.toString())
                    }

                    chatFragEd.text.clear()
                }
            }
        }
    }
}