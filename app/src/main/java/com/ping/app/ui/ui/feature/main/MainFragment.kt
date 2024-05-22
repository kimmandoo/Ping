package com.ping.app.ui.ui.feature.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ping.app.R
import com.ping.app.data.model.Gathering
import com.ping.app.databinding.FragmentMainBinding
import com.ping.app.ui.base.BaseFragment
import com.ping.app.ui.presentation.main.MainViewModel
import com.ping.app.ui.presentation.map.PingMapViewModel
import com.ping.app.ui.ui.util.easyToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val TAG = "MainFragment_싸피"

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>(R.layout.fragment_main) {
    
    override val viewModel: MainViewModel by viewModels()
    private val pingMapViewModel: PingMapViewModel by activityViewModels()
    private lateinit var joinedData: Gathering
    private val mainAdapter by lazy {
        MainAdapter(onMoveDetailedConfirmation = {
            onMoveDetailedConfirmation(it)
        }, onEnterCodeDialog = {
            if(::joinedData.isInitialized && joinedData == it){
                onMoveDetailedConfirmation(it)
            }else{
                onEnterCodeDialog(it)
            }
        })
    }
    
    override fun initView(savedInstanceState: Bundle?) {
        viewModel.mainToMapShortCutInit()
        
        lifecycleScope.launch {
            viewModel.mainToMapShortCut.observe(viewLifecycleOwner) { shortCutGatheringData ->
                // LiveData가 변경될 때 UI 업데이트
                binding.mainFragLinearPlannedParticipationResult.visibility =
                    if (shortCutGatheringData != null) {
                        joinedData = shortCutGatheringData
                        Log.d(TAG, "initView: $shortCutGatheringData")
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                shortCutGatheringData?.let {
                    binding.mainFragLinearPlannedParticipationResult.setOnClickListener {
                        val actionMainToMap =
                            MainFragmentDirections.actionMainFragmentToPingMapFragment(
                                shortCutGatheringData
                            )
                        findNavController().navigate(actionMainToMap)
                    }
                }
            }
        }

        binding.apply {
            mainFragToChatgpt.setOnClickListener{
                findNavController().navigate(R.id.action_mainFragment_to_mainFragToChatgpt)
            }
        }



        val user = viewModel.getUserInfo()
        
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.duplicatedState.collectLatest { isDuplicated ->
                    binding.mainFragFab.apply {
                        visibility = if (!isDuplicated) View.GONE else View.VISIBLE
                        setOnClickListener {
                            if (isDuplicated) {
                                findNavController().navigate(R.id.action_mainFragment_to_pingAddMapFragment)
                            } else {
                                binding.root.context.easyToast(getString(R.string.main_already_table))
                            }
                        }
                    }
                }
            }
        }
        
        binding.apply {
            mainFragTitleHello.text =
                getString(R.string.main_user, user.displayName)
            Glide.with(binding.root.context).load(user.photoUrl).circleCrop().into(mainFragProfile)
            logout.setOnClickListener {
                lifecycleScope.launch {
                    logout.isEnabled = false
                    viewModel.logout()
                    logout.isEnabled = true
                    findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                }
            }
            mainFragRecyclerview.adapter = mainAdapter
        }
        
        viewModel.meetingList.observe(viewLifecycleOwner) { meetinglist ->
            meetinglist?.let {
                lifecycleScope.launch {
                    if (binding.mainSwitch.isChecked){
                        mainAdapter.submitList(meetinglist)
                    }else{
                        mainAdapter.submitList(meetinglist.filter { it.gatheringTime.toLong() > System.currentTimeMillis() })
                    }
                    binding.mainSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                        when (isChecked) {
                            true -> {
                                mainAdapter.submitList(meetinglist)
                                binding.root.context.easyToast(getString(R.string.main_see_all))
                            }
                            
                            false -> {
                                mainAdapter.submitList(meetinglist.filter { it.gatheringTime.toLong() > System.currentTimeMillis() })
                            }
                        }
                    }
                }
            }
        }
        
        
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pingMapViewModel.userLocation.collectLatest { currentLocation ->
                    currentLocation?.let {
                        val lat = currentLocation.latitude
                        val lng = currentLocation.longitude
                        
                        viewModel.getMeetingList(lat, lng)
                        Firebase.firestore.collection("MEETING")
                            .addSnapshotListener { snapshot, error ->
                                Log.d(TAG, "initView: ")
                                snapshot?.let { data ->
                                    viewModel.getMeetingList(lat, lng)
                                    viewModel.mainToMapShortCutInit()
                                }
                            }
                    }
                }
            }
        }
    }
    
    private fun onMoveDetailedConfirmation(gathering: Gathering) {
        val actionMainToMap = MainFragmentDirections.actionMainFragmentToPingMapFragment(gathering)
        findNavController().navigate(actionMainToMap)
    }
    
    private fun onEnterCodeDialog(gathering: Gathering) {
        val mainDialog = MainAlertDialog(binding.root.context, gathering)
        mainDialog.alertDialog.apply {
            setOnCancelListener {
                lifecycleScope.launch {
                    val actionMainToMap =
                        MainFragmentDirections.actionMainFragmentToPingMapFragment(gathering)
                    findNavController().navigate(actionMainToMap)
                }
            }
        }
        mainDialog.showDialog()
    }
}