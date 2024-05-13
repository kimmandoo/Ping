package com.ping.app.ui.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PingMapViewModel : ViewModel() {
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation get() = _userLocation.asStateFlow()
    
    fun setUserLocation(currentLocation: LatLng?) {
        viewModelScope.launch(Dispatchers.IO) {
            _userLocation.emit(currentLocation)
        }
    }
}