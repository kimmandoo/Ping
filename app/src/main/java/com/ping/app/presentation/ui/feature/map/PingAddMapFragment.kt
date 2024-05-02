package com.ping.app.presentation.ui.feature.map

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.UiThread
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.ping.app.R
import com.ping.app.databinding.FragmentPingMapAddBinding
import com.ping.app.presentation.base.BaseFragment
import com.ping.app.presentation.util.GPS_ENABLE_REQUEST_CODE
import com.ping.app.presentation.util.LocationHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

private const val TAG = "PingAddMapFragment_싸피"

class PingAddMapFragment :
    BaseFragment<FragmentPingMapAddBinding, PingMapViewModel>(R.layout.fragment_ping_map_add),
    OnMapReadyCallback {
    override val viewModel: PingMapViewModel by activityViewModels()
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var currentPosition: LatLng
    
    private val locationHelperInstance by lazy {
        LocationHelper.getInstance()
    }
    
    override fun initView(savedInstanceState: Bundle?) {
        // flow
        // 현재 위치 찾을때까지 로딩바
        // 현재 위치 찾고나서 지도 뷰 보여줌(주소로 검색?도 해보자(심화기능))
        // 핑 찍을 곳에 longclick하면 핑 찍히게
        // longclick하면 dialog로 상세정보 설정하도록 하기
        // 상세정보 - 위치, 주최자, 날짜(핑 모집, 언제 볼건지), 인원, 참여가능한 사람(친구만, 전체)
        locationHelperInstance.startLocationTracking()
        locationHelperInstance.listener = {
            viewModel.setUserLocation(LatLng(it))
        }
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_add_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_add_view, it).commit()
            }
        mapFragment.getMapAsync(this)

//        mapView.onCreate(savedInstanceState)
//        mapView.getMapAsync(this@PingAddMapFragment)
        locationSource =
            FusedLocationSource(this, GPS_ENABLE_REQUEST_CODE)
        
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userLocation.collectLatest { currentLocation ->
                    currentLocation?.let {
                        binding.mapFragmentView.visibility = View.VISIBLE
                        binding.mapProgress.visibility = View.GONE
                        currentPosition = currentLocation
                        if (::naverMap.isInitialized) {
                            naverMap.locationOverlay.run {
                                isVisible = true
                                position =
                                    LatLng(currentLocation.latitude, currentLocation.longitude)
                            }
                            binding.mapReset.visibility =
                                if (naverMap.cameraPosition.target.distanceTo(
                                        LatLng(
                                            currentLocation.latitude,
                                            currentLocation.longitude
                                        )
                                    ) > 150
                                ) {
                                    View.VISIBLE
                                } else {
                                    View.GONE
                                }
                        }
                    }
                }
            }
        }
    }
    
    @SuppressLint("MissingPermission")
    @UiThread
    override fun onMapReady(map: NaverMap) {
        naverMap = map
        map.locationSource = locationSource
        map.apply {
            minZoom = 14.0
            maxZoom = 18.0
            cameraPosition = CameraPosition(
                LatLng(
                    map.cameraPosition.target.latitude,
                    map.cameraPosition.target.longitude
                ), 16.0
            )
            uiSettings.apply {
                // zoom 버튼 제거하기
                isZoomControlEnabled = false
            }
        }
        val marker = Marker()
        marker.apply {
            position = LatLng(
                map.cameraPosition.target.latitude,
                map.cameraPosition.target.longitude
            )
            icon = OverlayImage.fromResource(R.drawable.map_point_wave_svgrepo_com)
            iconTintColor = Color.RED
        }
        marker.map = map
        map.addOnCameraChangeListener { type, animated ->
            marker.position = LatLng(
                // 현재 보이는 네이버맵의 정중앙 가운데로 마커 이동
                map.cameraPosition.target.latitude,
                map.cameraPosition.target.longitude
            )
            // 주소 텍스트 세팅 및 확인 버튼 비활성화
//            binding.tvLocation.run {
//                text = "위치 이동 중"
//                setTextColor(Color.parseColor("#c4c4c4"))
//            }
//            binding.btnConfirm.run {
//                setBackgroundResource(R.drawable.rect_round_c4c4c4_radius_8)
//                setTextColor(Color.parseColor("#ffffff"))
//                isEnabled = false
//            }
        }
        // 카메라의 움직임 종료에 대한 이벤트 리스너 인터페이스.
        map.addOnCameraIdleListener {
            marker.position = LatLng(
                map.cameraPosition.target.latitude,
                map.cameraPosition.target.longitude
            )
            binding.mapJuso.text = getAddress(marker.position.latitude, marker.position.longitude)
            Log.d(
                TAG,
                "onMapReady: ${getAddress(marker.position.latitude, marker.position.longitude)}"
            )
//            // 좌표 -> 주소 변환 텍스트 세팅, 버튼 활성화
//            binding.tvLocation.run {
//                text = getAddress(
//                    naverMap.cameraPosition.target.latitude,
//                    naverMap.cameraPosition.target.longitude
//                )
//                setTextColor(Color.parseColor("#2d2d2d"))
//            }
//            binding.btnConfirm.run {
//                setBackgroundResource(R.drawable.rect_round_ffd464_radius_8)
//                setTextColor(Color.parseColor("#FF000000"))
//                isEnabled = true
//            }
        }
        
        // 사용자 현재 위치 받아오기
        locationHelperInstance.getClient().lastLocation
            .addOnSuccessListener { location: Location ->
                map.locationOverlay.run {
                    isVisible = true
                    position = LatLng(location.latitude, location.longitude)
                }
                
                val cameraUpdate = CameraUpdate.scrollTo(
                    LatLng(
                        location.latitude,
                        location.longitude
                    )
                )
                map.moveCamera(cameraUpdate)
                
                marker.position = LatLng(
                    map.cameraPosition.target.latitude,
                    map.cameraPosition.target.longitude
                )
            }
        
        binding.mapReset.setOnClickListener {
            if (::currentPosition.isInitialized) {
                val resetCamera = CameraUpdate.scrollTo(currentPosition)
                map.moveCamera(resetCamera.animate(CameraAnimation.Easing))
            }
        }
        
    }
    
    private fun getAddress(lat: Double, lng: Double): String {
        val geoCoder = Geocoder(requireContext(), Locale.KOREA)
        var addressResult = "주소를 가져 올 수 없습니다."
        runCatching {
            geoCoder.getFromLocation(lat, lng, 1) as ArrayList<Address>
        }.onSuccess { response ->
            if (response.size > 0) {
                val currentLocationAddress = response[0].getAddressLine(0)
                    .toString()
                addressResult = currentLocationAddress
            }
        }.onFailure {
            Log.d(TAG, "error: ${it.stackTrace}")
        }
        
        return addressResult
    }
}