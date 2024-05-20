package com.ping.app.ui.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ping.app.data.model.Gathering
import com.ping.app.data.repository.login.LoginRepoImpl
import com.ping.app.data.repository.main.MainRepoImpl
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel_싸피"
class MainViewModel(): ViewModel() {
    private val mainInstance = MainRepoImpl.get()
    private val loginInstance = LoginRepoImpl.get()
    private val _meetingList = MutableLiveData<List<Gathering>>()
    val meetingList : LiveData<List<Gathering>> get() = _meetingList

    private val _mainToMapShortCut = MutableLiveData<Gathering?>()
    val mainToMapShortCut : LiveData<Gathering?> get() = _mainToMapShortCut

    fun mainToMapShortCutTest(){
        _mainToMapShortCut.value = null
        viewModelScope.launch {
            _mainToMapShortCut.value = mainInstance.meetingsToAttend(loginInstance.getAccessToken())
        }
    }

    private fun updateMeetingList(newList: List<Gathering>){
        _meetingList.value = newList
    }
    
    fun initMeetingList(lat: Double, lng: Double) {
        viewModelScope.launch {
            updateMeetingList(mainInstance.getMeetingTable(lng, lat))
        }
    }
}