package com.ping.app.ui.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ping.app.data.model.Gathering

class MainViewModel(): ViewModel() {
    private val _meetingList = MutableLiveData<List<Gathering>>()

    val meetingList : LiveData<List<Gathering>>
        get() = _meetingList


    fun updateMeetingList(newList: List<Gathering>){
        _meetingList.value = newList
    }
}