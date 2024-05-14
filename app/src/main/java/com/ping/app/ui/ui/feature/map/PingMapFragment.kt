package com.ping.app.ui.ui.feature.map

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.UiThread
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.ping.app.PingApplication
import com.ping.app.R
import com.ping.app.data.model.Gathering
import com.ping.app.databinding.FragmentPingMapBinding
import com.ping.app.ui.presentation.map.PingMapViewModel
import com.ping.app.ui.base.BaseFragment
import com.ping.app.ui.ui.util.Map.GPS_ENABLE_REQUEST_CODE
import com.ping.app.ui.ui.util.Map.MAP_BOUNDS
import com.ping.app.ui.ui.util.round
import com.ping.app.ui.ui.util.starbucks
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val TAG = "MapFragment 싸피"

class PingMapFragment :
    BaseFragment<FragmentPingMapBinding, PingMapViewModel>(R.layout.fragment_ping_map),
    OnMapReadyCallback {

    override val viewModel: PingMapViewModel by activityViewModels()
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var dataFromMain: Gathering
    private val args: PingMapFragmentArgs by navArgs()
    private val locationHelperInstance by lazy {
        PingApplication.locationHelper
    }

    override fun initView(savedInstanceState: Bundle?) {
        args.pingData?.let {
            Log.d(TAG, "initView: $it")
            dataFromMain = it
        }

        locationHelperInstance.startLocationTracking()
        locationHelperInstance.listener = {
            viewModel.setUserLocation(LatLng(it))
        }
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this@PingMapFragment)
        locationSource =
            FusedLocationSource(this, GPS_ENABLE_REQUEST_CODE)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userLocation.collectLatest { currentLocation ->
                    currentLocation?.let {
                        binding.mapFragmentView.visibility = View.VISIBLE
                        binding.mapProgress.visibility = View.GONE
                        binding.mapDistance.text =
                            String.format("%.2f", currentLocation.distanceTo(starbucks))
                        if (::naverMap.isInitialized) {
                            naverMap.locationOverlay.run {
                                isVisible = true
                                position =
                                    LatLng(currentLocation.latitude, currentLocation.longitude)
                            }
                        }
                    }
                }
            }
        }
    }

    @UiThread
    override fun onMapReady(map: NaverMap) {
        val pingAlert = PingAlertDialog(binding.root.context)
        // 이 화면은 일정을 누르면 나올것이기 때문에 객체로 넘어오는 lat, lng값을 지도의 초기 위치로 잡고, 마커를 띄운다.
        naverMap = map
        naverMap.locationSource = locationSource
        naverMap.apply {
            uiSettings.apply {
                // zoom 버튼 제거하기
                isZoomControlEnabled = false
            }
            minZoom = 16.0
            maxZoom = 18.0
            var pingPosition = starbucks
            if (::dataFromMain.isInitialized){
                pingPosition = LatLng(dataFromMain.latitude, dataFromMain.longitude)
            }
            cameraPosition = CameraPosition(
                LatLng(
                    pingPosition.latitude,
                    pingPosition.longitude
                ),
                16.0
            )
            extent = LatLngBounds(
                pingPosition.offset(-MAP_BOUNDS, -MAP_BOUNDS),
                pingPosition.offset(MAP_BOUNDS, MAP_BOUNDS)
            )

            addOnCameraIdleListener {
                val scale = round(cameraPosition.zoom - 16.0, 1) * MAP_BOUNDS
                extent = LatLngBounds(
                    starbucks.offset(-(MAP_BOUNDS + scale), -(MAP_BOUNDS + scale)),
                    starbucks.offset((MAP_BOUNDS + scale), (MAP_BOUNDS + scale))
                )
                // 핑 찍힌 위치에서 150m이상 멀어지면 카메라 이동 버튼 활성화
                binding.mapReset.visibility =
                    if (cameraPosition.target.distanceTo(pingPosition) > 150) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
            binding.mapReset.setOnClickListener {
                val resetCamera = CameraUpdate.scrollTo(pingPosition)
                moveCamera(resetCamera.animate(CameraAnimation.Easing))
            }
        }
        val marker = Marker()
        marker.apply {
            position = LatLng(
                naverMap.cameraPosition.target.latitude,
                naverMap.cameraPosition.target.longitude
            )
            icon = OverlayImage.fromResource(R.drawable.map_point_wave_svgrepo_com)
            iconTintColor = Color.RED
        }
        marker.map = naverMap

        binding.apply {
            mapBtnGathering.setOnClickListener {
                Log.d(TAG, "initView: ")
                pingAlert.showDialog()
                pingAlert.alertDialog.apply {
                    setOnCancelListener {
                        lifecycleScope.launch {
                            // 모임에 참여시키는 로직 들어가면 됨
                            findNavController().navigate(R.id.action_pingMapFragment_to_gatheringFragment)
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}