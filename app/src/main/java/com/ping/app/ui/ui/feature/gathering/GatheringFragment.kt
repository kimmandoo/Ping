package com.ping.app.ui.ui.feature.gathering

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.ping.app.R
import com.ping.app.databinding.FragmentGatheringBinding
import com.ping.app.ui.base.BaseFragment
import com.ping.app.ui.presentation.gathering.GatheringViewModel

class GatheringFragment: BaseFragment<FragmentGatheringBinding, GatheringViewModel>(R.layout.fragment_gathering) {
    
    override val viewModel: GatheringViewModel by viewModels()
    
    override fun initView(savedInstanceState: Bundle?) {
    
    }
}