package com.ping.app.ui.presentation.gathering

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

private const val TAG = "GatheringViewModel_싸피"
class GatheringViewModel : ViewModel() {
    private val _leftTime = MutableStateFlow<Long?>(System.currentTimeMillis()+60*60*24);
    val leftTime: StateFlow<Long?> get() = _leftTime
    
    private var oldTimeMills: Long = 0
    
    fun getLeftTime(targetTime: Long?=null): Job {
        return viewModelScope.launch {
            withContext(Dispatchers.IO) {
                oldTimeMills = System.currentTimeMillis()
                while (_leftTime.value!! > 0L) {
                    val delayMills = System.currentTimeMillis() - oldTimeMills
                    if (delayMills == 60*1000L) {
                        _leftTime.value = _leftTime.value!! - delayMills
                        oldTimeMills = System.currentTimeMillis()
                        Log.d(TAG, "initView: ${SimpleDateFormat("dd일 HH시 mm분 ss초").format(Date(_leftTime.value!!))} 남음")
                    }
                }
            }
        }
    }
}