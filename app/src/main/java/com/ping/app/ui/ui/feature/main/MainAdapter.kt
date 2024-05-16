package com.ping.app.ui.ui.feature.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ping.app.data.model.Gathering
import com.ping.app.databinding.ItemMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainAdapter(private val onMoveDetailedConfirmation: (Gathering) -> Unit) :
    ListAdapter<Gathering, MainAdapter.MainHolder>(
        diffUtil
    ) {
    inner class MainHolder(private val binding: ItemMainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(position: Int) {
            val item = currentList.get(position)
            binding.mainItemCard.setOnClickListener {
                onMoveDetailedConfirmation(item)
            }
            binding.mainItemTitle.text = item.title
//            binding.mainItemTimeRemaining.text = item.content
            CoroutineScope(Dispatchers.Default).launch {
                val targetTime = item.gatheringTime.toLong()
                withContext(Dispatchers.Main) {
                    val format = SimpleDateFormat("DD일 hh시 mm분 뒤", Locale.KOREA)
                    binding.mainItemTimeRemaining.text = format.format(
                        Date(
                            (targetTime - System.currentTimeMillis())
                        )
                    )
                }
            }
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            ItemMainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    
    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        // currentList: 해당 Adapter에 "submitList()"를 통해 삽입한 아이템 리스트
        holder.bindInfo(position)
    }
    
    
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Gathering>() {
            override fun areItemsTheSame(oldItem: Gathering, newItem: Gathering): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
            
            override fun areContentsTheSame(oldItem: Gathering, newItem: Gathering): Boolean {
                return oldItem == newItem
            }
            
        }
    }
    
}