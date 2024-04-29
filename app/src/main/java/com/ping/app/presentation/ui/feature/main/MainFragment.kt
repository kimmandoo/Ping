package com.ping.app.presentation.ui.feature.main

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ping.app.R
import com.ping.app.databinding.FragmentMainBinding
import com.ping.app.presentation.base.BaseFragment


class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>(R.layout.fragment_main) {
    override val viewModel: MainViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {

    }
}