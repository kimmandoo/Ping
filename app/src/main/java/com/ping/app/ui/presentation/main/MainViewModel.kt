package com.ping.app.ui.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ping.app.data.model.Gathering
import com.ping.app.data.repository.login.LoginRepoImpl
import com.ping.app.data.repository.main.MainRepoImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel_싸피"

class MainViewModel() : ViewModel() {
    private val mainInstance = MainRepoImpl.getInstance()
    private val loginInstance = LoginRepoImpl.getInstance()
    private val _meetingList = MutableLiveData<List<Gathering>>()
    val meetingList: LiveData<List<Gathering>> get() = _meetingList
    
    private val _mainToMapShortCut = MutableLiveData<Gathering?>()
    val mainToMapShortCut: LiveData<Gathering?> get() = _mainToMapShortCut
    private val _duplicatedState = MutableSharedFlow<Boolean>()
    val duplicatedState get() = _duplicatedState.asSharedFlow()

    private val _organizerShortCut = MutableLiveData<Gathering?>()
    val organizerShortCut : LiveData<Gathering?>
        get() = _organizerShortCut

    fun organizerShortCutInit(){
        _organizerShortCut.value = null
        viewModelScope.launch {
            _organizerShortCut.value = mainInstance.checkOrganizerMeetingTable(loginInstance.getAccessToken())
        }
    }
    
    fun mainToMapShortCutInit() {
        _mainToMapShortCut.value = null
        viewModelScope.launch {
            _mainToMapShortCut.value = mainInstance.getMeetingsToAttend(loginInstance.getAccessToken())
        }
    }
    
    fun getMeetingList(lat: Double, lng: Double) {
        viewModelScope.launch {
            _meetingList.value = mainInstance.getMeetingTableWithPosition(lng, lat)
            _duplicatedState.emit(isUserDuplicated())
        }
    }
    
    fun getUserInfo() = LoginRepoImpl.getInstance().getUserInfo()!!
    
    private suspend fun getUid() = LoginRepoImpl.getInstance().getAccessToken()
    
    private suspend fun isUserDuplicated() = MainRepoImpl.getInstance().checkMeetingDuplicate(getUid())
    
    suspend fun logout() = LoginRepoImpl.getInstance().logout()
    
}