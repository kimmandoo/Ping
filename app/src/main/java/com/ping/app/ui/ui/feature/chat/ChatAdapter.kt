package com.ping.app.ui.ui.feature.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ping.app.data.model.gpt.ChatBubble
import com.ping.app.databinding.ItemChatBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter : ListAdapter<ChatBubble, RecyclerView.ViewHolder>(diffUtil) {

    inner class ChatMeViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChatBubble) {
            binding.itemChatGpt.visibility = View.GONE
            binding.itemChatGptTime.visibility = View.GONE


            binding.itemChatMe.text = item.context
            binding.itemChatMeTime.text = timeSetting()
        }
    }

    inner class ChatGPTViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatBubble) {
            binding.itemChatMeTime.visibility = View.GONE
            binding.itemChatMe.visibility = View.GONE

            binding.itemChatGpt.text = item.context
            binding.itemChatGptTime.text = timeSetting()
        }
    }

    fun timeSetting(): String{
        val now = System.currentTimeMillis()
        val simpleDateFormat = SimpleDateFormat("a hh:mm", Locale.KOREAN).format(now)
        return simpleDateFormat
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LEFT -> ChatMeViewHolder(
                ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            RIGHT -> ChatGPTViewHolder(
                ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ChatMeViewHolder -> holder.bind(item)
            is ChatGPTViewHolder -> holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatBubble>() {
            override fun areItemsTheSame(oldItem: ChatBubble, newItem: ChatBubble): Boolean {
                return oldItem.hashCode() == newItem.hashCode() // assuming ChatBubble has a unique id field
            }

            override fun areContentsTheSame(oldItem: ChatBubble, newItem: ChatBubble): Boolean {
                return oldItem == newItem
            }
        }

        const val LEFT = 1
        const val RIGHT = 2
    }
}
