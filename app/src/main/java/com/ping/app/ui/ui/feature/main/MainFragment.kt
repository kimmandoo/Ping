package com.ping.app.ui.ui.feature.main

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.geometry.LatLng
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ping.app.PingApplication
import com.ping.app.R
import com.ping.app.data.model.Gathering
import com.ping.app.databinding.FragmentMainBinding
import com.ping.app.ui.base.BaseFragment
import com.ping.app.ui.feature.main.MainAdapter
import com.ping.app.ui.presentation.MainActivityViewModel
import com.ping.app.ui.presentation.main.MainViewModel
import com.ping.app.ui.presentation.map.PingMapViewModel
import com.ping.app.ui.ui.feature.map.PingMapFragmentDirections
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 툴바 (일단 나중에 하는 걸로)
 *
 * 마이 페이지? 같은
 * - 이룸
 * - 이메일
 *
 * 리사이클러뷰 (list adapter 작성 완)
 * MEETING TABLE에서 값을 가져 와야함
 * 해당 값은 ViewModel에 넣아서 observer로 관리
 *
 *
 */
private const val TAG = "MainFragment_싸피"
class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>(R.layout.fragment_main) {

    override val viewModel: MainViewModel by viewModels()
    private val mainInstance = PingApplication.mainRepo
    private val pingMapViewModel: PingMapViewModel by activityViewModels()
    private val mainActivityViewModel : MainActivityViewModel by activityViewModels()
    override fun initView(savedInstanceState: Bundle?) {

        var lat = 0.0
        var lng = 0.0
        
        
        

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                 pingMapViewModel.userLocation.collectLatest { currentLocation ->
                    Log.d(TAG, "init@@@@@@@@@View: ${currentLocation}")
                    if (lat == 0.0 && lng == 0.0) {
                        if (currentLocation != null) {
                            lat = currentLocation.latitude
                            lng = currentLocation.longitude
                        }

                        initMeetingList(lat, lng)
                    }
                }
            }
        }

        lifecycleScope.launch {
            initMeetingList(lat, lng)
        }
        val mainAdapter = MainAdapter(onMoveDetailedConfirmation = {
//            Log.d(TAG, "initView: ${it}")
//            val action = MainFragmentDirections.actionMainFragmentToPingMapFragment(pingData = it)
//            findNavController().navigate(action)
            findNavController().navigate(R.id.action_mainFragment_to_pingMapFragment)
            mainInstance.participantsMeetingDetailTable(it, mainActivityViewModel.userUid.value.toString())
        })

        binding.mainFragRecyclerview.adapter = mainAdapter
        viewModel.meetingList.observe(viewLifecycleOwner, Observer { meetinglist ->
            meetinglist?.let { mainAdapter.submitList(meetinglist) }
        })
        
        binding.mainFragFab.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_pingAddMapFragment)
        }

    }


    suspend fun initMeetingList(lat: Double, lng: Double){

        val updateList = CompletableDeferred<List<Gathering>>()
        var getGathering = listOf<Gathering>()
        lifecycleScope.launch {
            getGathering = mainInstance.getMeetingTable(lng, lat)
            updateList.complete(getGathering)
        }
        updateList.await()
        viewModel.updateMeetingList(getGathering)

    }
}