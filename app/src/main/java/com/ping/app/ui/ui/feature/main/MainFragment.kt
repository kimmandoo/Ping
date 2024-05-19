package com.ping.app.ui.ui.feature.main

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ping.app.R
import com.ping.app.data.model.Gathering
import com.ping.app.data.repository.login.LoginRepoImpl
import com.ping.app.data.repository.main.MainRepoImpl
import com.ping.app.databinding.FragmentMainBinding
import com.ping.app.ui.base.BaseFragment
import com.ping.app.ui.presentation.main.MainViewModel
import com.ping.app.ui.presentation.map.PingMapViewModel
import com.ping.app.ui.ui.util.easyToast
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val TAG = "MainFragment_싸피"

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>(R.layout.fragment_main) {
    
    override val viewModel: MainViewModel by viewModels()
    private val pingMapViewModel: PingMapViewModel by activityViewModels()
    
    private val mainAdapter by lazy {
        MainAdapter(onMoveDetailedConfirmation = {
            onMoveDetailedConfirmation(it)
        }, onEnterCodeDialog = {
            onEnterCodeDialog(it)
        })
    }

    override fun initView(savedInstanceState: Bundle?) {

//        lifecycleScope.launch {
//            MainRepoImpl.get().meetingDuplicateCheck(LoginRepoImpl.get().getAccessToken())
//        }

        val user = LoginRepoImpl.get().getUserInfo()!!
        binding.apply {
            mainFragTitleHello.text =
                getString(R.string.main_user, user.displayName)
            Glide.with(binding.root.context).load(user.photoUrl).circleCrop().into(mainFragProfile)
            logout.setOnClickListener {
                lifecycleScope.launch {
                    logout.isEnabled = false
                    LoginRepoImpl.get().logout()
                    logout.isEnabled = true
                    findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                }
            }
            mainFragRecyclerview.adapter = mainAdapter
            mainFragFab.setOnClickListener {
                val duplicateDeffer = CompletableDeferred<Boolean>()
                var duplucateResult = false
                lifecycleScope.launch {
                    duplicateDeffer.complete(MainRepoImpl.get().meetingDuplicateCheck(LoginRepoImpl.get().getAccessToken()))
                    duplucateResult = duplicateDeffer.await()
                }

                if(duplucateResult == true) {
                    findNavController().navigate(R.id.action_mainFragment_to_pingAddMapFragment)
                }
                else{
                    binding.root.context.easyToast(getString(R.string.main_already_table))
                }
            }
        }
        
        viewModel.meetingList.observe(viewLifecycleOwner) { meetinglist ->
            meetinglist?.let {
                mainAdapter.submitList(meetinglist.filter { it.gatheringTime.toLong() > System.currentTimeMillis() })
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
        
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pingMapViewModel.userLocation.collectLatest { currentLocation ->
                    currentLocation?.let {
                        val lat = currentLocation.latitude
                        val lng = currentLocation.longitude
                        
                        viewModel.initMeetingList(lat, lng)
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