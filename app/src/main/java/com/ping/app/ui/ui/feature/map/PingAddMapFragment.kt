package com.ping.app.ui.ui.feature.map

import android.os.Bundle
import android.util.Log
import androidx.annotation.UiThread
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.ping.app.PingApplication
import com.ping.app.R
import com.ping.app.databinding.FragmentPingMapAddBinding
import com.ping.app.ui.base.BaseFragment
import com.ping.app.ui.presentation.map.PingMapViewModel
import com.ping.app.ui.ui.util.CLICK_DELAY
import com.ping.app.ui.ui.util.Map.GPS_ENABLE_REQUEST_CODE
import com.ping.app.ui.ui.util.Map.USER_POSITION_LAT
import com.ping.app.ui.ui.util.Map.USER_POSITION_LNG
import com.ping.app.ui.ui.util.init
import com.ping.app.ui.ui.util.withMarker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "PingAddMapFragment_싸피"

class PingAddMapFragment :
    BaseFragment<FragmentPingMapAddBinding, PingMapViewModel>(R.layout.fragment_ping_map_add),
    OnMapReadyCallback {
    override val viewModel: PingMapViewModel by activityViewModels()
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private val locationHelperInstance = PingApplication.locationHelper
    
    override fun initView(savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_add_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.map_add_view, it).commit()
            }
        mapFragment.getMapAsync(this)
        locationSource =
            FusedLocationSource(this, GPS_ENABLE_REQUEST_CODE)
    }
    
    @UiThread
    override fun onMapReady(map: NaverMap) {
        val marker = Marker()
        naverMap = map
        map.locationSource = locationSource
        map.init()
        marker.init(map)
        marker.map = map
        map.setOnSymbolClickListener {
            marker.captionText = it.caption
            marker.position = it.position
            true
        }
        map.withMarker(marker, binding.pingAddView)
        locationHelperInstance.getClient().init(map, marker, binding.location)
        binding.mapAddBtn.setOnClickListener {
            lifecycleScope.launch {
                binding.mapAddBtn.isEnabled = false
                createPing(map, marker)
                delay(CLICK_DELAY)
                binding.mapAddBtn.isEnabled = true
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        
    }
    
    private fun createPing(map: NaverMap, marker: Marker) {
        Log.d(TAG, "createPing: ${map.cameraPosition}")
        val userPosition = bundleOf(
            USER_POSITION_LAT to marker.position.latitude,
            USER_POSITION_LNG to marker.position.longitude,
            "symbol" to marker.captionText
        )
        val modal = PingAddPostFragment()
        modal.arguments = userPosition
        modal.show(childFragmentManager, "modal")
    }
}