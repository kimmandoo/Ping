package com.ping.app.ui.ui.feature.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ping.app.R
import com.ping.app.data.model.Gathering
import com.ping.app.databinding.ItemMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainAdapter(private val onMoveDetailedConfirmation: (Gathering) -> Unit, private val onEnterCodeDialog: (Gathering) -> Unit) :
    ListAdapter<Gathering, MainAdapter.MainHolder>(
        diffUtil
    ) {
    inner class MainHolder(private val binding: ItemMainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(position: Int) {
            val item = currentList[position]
            binding.mainItemCard.setOnClickListener {
                if (item.enterCode == "") {
                    onMoveDetailedConfirmation(item)
                }else {
                    onEnterCodeDialog(item)
                }
            }

            if(item.enterCode != ""){
                binding.enterOrPass.setImageResource(R.drawable.baseline_lock_24)
            }else{
                binding.enterOrPass.setImageResource(R.drawable.baseline_arrow_forward_ios_24)
            }

            binding.mainItemTitle.text = item.title
//            binding.mainItemTimeRemaining.text = item.content
            val targetTime = item.gatheringTime.toLong()
            CoroutineScope(Dispatchers.Default).launch {
                flow {
                    while (true) {
                        emit(targetTime - System.currentTimeMillis())
                        delay(1000*60)
                    }
                }.takeWhile {
                    remainingMillis -> remainingMillis > 0
                }.onCompletion {
                    withContext(Dispatchers.Main) {
                        binding.mainItemTimeRemaining.text = "마감"
                        binding.view1.setBackgroundColor(Color.RED)
                    }
                }.collect { remainingMillis ->
                    val totalSeconds = remainingMillis / 1000
                    val remainingMinutes = (totalSeconds / 60) % 60
                    val remainingHours = (totalSeconds / 3600) % 24
                    val remainingDays = totalSeconds / (24 * 3600)

                    withContext(Dispatchers.Main) {
                        binding.view1.setBackgroundColor(Color.BLACK)
                        binding.mainItemTimeRemaining.text =
                            "${remainingDays}일 ${remainingHours}시 ${remainingMinutes}분 뒤"
                    }
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