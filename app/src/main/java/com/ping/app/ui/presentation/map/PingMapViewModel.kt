package com.ping.app.ui.presentation.map

import android.util.Log
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

private const val TAG = "PingMapViewModel_싸피"
class PingMapViewModel : ViewModel() {
    private val locationHelperInstance = LocationHelper.getInstance()
    private val pingMapInstance = PingMapRepoImpl.getInstance()
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation get() = _userLocation.asStateFlow()
    
    init {
        Log.d(TAG, ": viewModel created")
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
    
    fun requestAddress(lat: Double, lng: Double) = pingMapInstance.requestAddress(lat, lng)
    
    fun sendPingInfo(gathering: Gathering){
        pingMapInstance.sendPingInfo(
            gathering
        )
    }
    
    fun getLocationClient() = locationHelperInstance.getClient()
}