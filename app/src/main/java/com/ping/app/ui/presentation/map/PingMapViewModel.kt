package com.ping.app.ui.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.ping.app.data.model.Gathering
import com.ping.app.data.repository.main.MainRepoImpl
import com.ping.app.data.repository.map.PingMapRepoImpl
import com.ping.app.ui.ui.util.LocationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PingMapViewModel : ViewModel() {
    private val locationHelperInstance = LocationHelper.getInstance()
    private val pingMapInstance = PingMapRepoImpl.getInstance()
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation get() = _userLocation.asStateFlow()
    
    init {
        locationHelperInstance.startLocationTracking()
        locationHelperInstance.listener = {
            setUserLocation(LatLng(it))
        }
    }
    
    fun setUserLocation(currentLocation: LatLng?) {
        viewModelScope.launch(Dispatchers.IO) {
            _userLocation.emit(currentLocation)
        }
    }
    
    suspend fun getUserName(uid: String) = pingMapInstance.getUserName(uid)
    
    fun organizercancellationOfParticipantsMeetingTable(gathering: Gathering, uid: String) {
        pingMapInstance.organizercancellationOfParticipantsMeetingTable(gathering, uid)
    }
    
    fun cancellationOfParticipantsMeetingDetailTable(gathering: Gathering, uid: String) {
        pingMapInstance.cancellationOfParticipantsMeetingDetailTable(gathering, uid)
    }
    
    fun participantsMeetingDetailTable(gathering: Gathering, uid: String) {
        pingMapInstance.participantsMeetingDetailTable(gathering, uid)
    }
    
    suspend fun isExist(gathering: Gathering, uid: String) =
        MainRepoImpl.get().detailMeetingDuplicateCheck(gathering, uid)
}