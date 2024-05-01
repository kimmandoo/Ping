package com.ping.app.presentation.ui.feature.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.launch

class PingMapViewModel : ViewModel() {
    private val _userLocation = MutableLiveData<LatLng>()
    val userLocation: LiveData<LatLng> = _userLocation
    
    fun setUserLocation(currentLocation: LatLng?) {
        viewModelScope.launch {
            currentLocation?.let {
                _userLocation.postValue(it)
            }
        }
    }
}