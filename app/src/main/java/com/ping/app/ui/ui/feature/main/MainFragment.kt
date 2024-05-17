package com.ping.app.ui.ui.feature.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ping.app.PingApplication
import com.ping.app.R
import com.ping.app.data.model.Gathering
import com.ping.app.data.repository.login.LoginRepoImpl
import com.ping.app.databinding.FragmentMainBinding
import com.ping.app.ui.base.BaseFragment
import com.ping.app.ui.presentation.MainActivityViewModel
import com.ping.app.ui.presentation.main.MainViewModel
import com.ping.app.ui.presentation.map.PingMapViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val TAG = "MainFragment_싸피"

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>(R.layout.fragment_main) {

    override val viewModel: MainViewModel by viewModels()
    private val mainInstance = PingApplication.mainRepo
    private val pingMapViewModel: PingMapViewModel by activityViewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    override fun initView(savedInstanceState: Bundle?) {

        lifecycleScope.launch {
            val gatheringTable = mainInstance.meetingsToAttend(mainActivityViewModel.userUid.value.toString())
        }


        val user = LoginRepoImpl.get().getUserInfo()!!
        binding.apply {
            mainFragTitleHello.text =
                getString(R.string.main_user, user.displayName)
            Glide.with(binding.root.context).load(user.photoUrl).circleCrop().into(mainFragProfile)
            binding.logout.setOnClickListener {
                lifecycleScope.launch {
                    Log.d(TAG, "initView: 로그아웃 시작")
                    LoginRepoImpl.get().logout()
                    Log.d(TAG, "initView: 로그아웃 완료")
                    findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pingMapViewModel.userLocation.collectLatest { currentLocation ->
                    Log.d(TAG, "init@@@@@@@@@View: ${currentLocation}")
                    currentLocation?.let {
                        val lat = currentLocation.latitude
                        val lng = currentLocation.longitude

                        initMeetingList(lat, lng)
                    }
                }
            }
        }

        val mainAdapter = MainAdapter(onMoveDetailedConfirmation = {
            val actionMainToMap = MainFragmentDirections.actionMainFragmentToPingMapFragment(it)
            findNavController().navigate(actionMainToMap)
            mainInstance.participantsMeetingDetailTable(
                it,
                mainActivityViewModel.userUid.value.toString()
            )
        })

        binding.mainFragRecyclerview.adapter = mainAdapter
        viewModel.meetingList.observe(viewLifecycleOwner, Observer { meetinglist ->
            meetinglist?.let { mainAdapter.submitList(meetinglist) }
        })
        
        binding.mainFragFab.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_pingAddMapFragment)
        }

    }


    suspend fun initMeetingList(lat: Double, lng: Double) {
        val updateList = CompletableDeferred<List<Gathering>>()

        lifecycleScope.launch {
            updateList.complete(mainInstance.getMeetingTable(lng, lat))
        }
        viewModel.updateMeetingList(updateList.await())

    }
}