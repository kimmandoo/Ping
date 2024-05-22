package com.ping.app.ui.ui.feature.map

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.UiThread
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import com.ping.app.R
import com.ping.app.data.model.Gathering
import com.ping.app.databinding.FragmentPingMapBinding
import com.ping.app.ui.base.BaseFragment
import com.ping.app.ui.presentation.login.LoginViewModel
import com.ping.app.ui.presentation.map.PingMapViewModel
import com.ping.app.ui.ui.util.Map.GPS_ENABLE_REQUEST_CODE
import com.ping.app.ui.ui.util.Map.MAP_BOUNDS
import com.ping.app.ui.ui.util.easyToast
import com.ping.app.ui.ui.util.round
import com.ping.app.ui.ui.util.starbucks
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val TAG = "MapFragment 싸피"

class PingMapFragment :
    BaseFragment<FragmentPingMapBinding, PingMapViewModel>(R.layout.fragment_ping_map),
    OnMapReadyCallback {

    override val viewModel: PingMapViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var dataFromMain: Gathering
    private lateinit var latlngFromMain: LatLng
    private val args: PingMapFragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {

        args.pingData?.let {
            Log.d(TAG, "initView: $it")
            dataFromMain = it
            latlngFromMain = LatLng(dataFromMain.latitude, dataFromMain.longitude)
        }

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this@PingMapFragment)
        locationSource =
            FusedLocationSource(this, GPS_ENABLE_REQUEST_CODE)

        lifecycleScope.launch {
            initUi(viewModel.isExist(dataFromMain, loginViewModel.getUid()))
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userLocation.collectLatest { currentLocation ->
                    currentLocation?.let {
                        binding.apply {
                            mapDataOrganizer.text = getString(
                                R.string.map_data_title,
                                dataFromMain.organizer
                            )
                            val dist = if (currentLocation.distanceTo(latlngFromMain) < 10) {
                                "0m"
                            } else {
                                currentLocation.distanceTo(latlngFromMain).toInt().toString() + "m"
                            }
                            mapDataWhere.text =
                                getString(R.string.ping_map_location, dataFromMain.title, dist)
                            mapDataContent.text =
                                getString(R.string.map_data_content, dataFromMain.content)
                            mapFragmentView.visibility = View.VISIBLE
                            mapProgress.visibility = View.GONE
                        }
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

    @SuppressLint("ClickableViewAccessibility")
    @UiThread
    override fun onMapReady(map: NaverMap) {
        // 이 화면은 일정을 누르면 나올것이기 때문에 객체로 넘어오는 lat, lng값을 지도의 초기 위치로 잡고, 마커를 띄운다.
        naverMap = map
        mapView.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    // 터치 다운 또는 터치 이동 시 부모 스크롤뷰의 터치를 막습니다.
                    mapView.parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 터치 업 또는 터치 취소 시 부모 스크롤뷰의 터치를 허용합니다.
                    mapView.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
        naverMap.locationSource = locationSource
        naverMap.apply {
            uiSettings.apply {
                isZoomControlEnabled = false
            }
            minZoom = 16.0
            maxZoom = 18.0
            var pingPosition = starbucks
            if (::dataFromMain.isInitialized) {
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
                    pingPosition.offset(-(MAP_BOUNDS + scale), -(MAP_BOUNDS + scale)),
                    pingPosition.offset((MAP_BOUNDS + scale), (MAP_BOUNDS + scale))
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
            captionText = dataFromMain.title
        }
        marker.map = naverMap
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initUi(stateJoin: Boolean) {
        val pingAlert = PingAlertDialog(binding.root.context)

        if (dataFromMain.gatheringTime.toLong() < System.currentTimeMillis()) {
            binding.mapBtnGathering.apply {
                text = "종료"
                isEnabled = false
            }
        } else {
            if (stateJoin) {
                binding.mapBtnGathering.apply {
                    lifecycleScope.launch {
                        val uid = loginViewModel.getUid()
                        text = if (uid == dataFromMain.uid) "삭제하기" else "취소하기"
                        setBackgroundColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.ping_red,
                                null
                            )
                        )
                        setOnClickListener {
                            val dialog = if (uid == dataFromMain.uid) {
                                PingAlertCancelDialog(binding.root.context, "삭제하시겠습니까?")
                            } else {
                                PingAlertCancelDialog(binding.root.context, "취소하시겠습니까?")
                            }
                            dialog.showDialog()
                            dialog.alertDialog.setOnCancelListener {
                                if (dataFromMain.uid == uid) {
                                    viewModel.organizercancellationOfParticipantsMeetingTable(
                                        dataFromMain,
                                        uid
                                    )
                                    binding.root.context.easyToast("핑이 삭제되었습니다")
                                    findNavController().popBackStack()
                                } else {
                                    viewModel.cancellationOfParticipantsMeetingDetailTable(
                                        dataFromMain,
                                        uid
                                    )
                                    binding.mapBtnGathering.apply {
                                        setBackgroundColor(
                                            ResourcesCompat.getColor(
                                                resources,
                                                R.color.ic_launcher_background,
                                                null
                                            )
                                        )
                                        text = getString(R.string.join)
                                    }
                                    binding.root.context.easyToast("참여 취소되었습니다")
                                    initUi(false)
                                }
                            }
                        }
                    }
                }
            } else {
                binding.mapBtnGathering.setOnClickListener {
                    pingAlert.showDialog()
                    pingAlert.alertDialog.apply {
                        setOnCancelListener {
                            lifecycleScope.launch {
                                viewModel.participantsMeetingDetailTable(
                                    dataFromMain,
                                    loginViewModel.getUid()
                                )
                                dismiss()
                                initUi(true)
                            }
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