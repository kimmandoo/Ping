package com.ping.app.ui.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    private val _userUid = MutableLiveData<String>()

    val userUid : LiveData<String>
        get() = _userUid


    fun saveUserUid(uid: String){
        _userUid.postValue(uid)
    }
}