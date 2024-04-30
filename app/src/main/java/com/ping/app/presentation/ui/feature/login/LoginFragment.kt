package com.ping.app.presentation.ui.feature.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ping.app.R
import com.ping.app.databinding.FragmentLoginBinding
import com.ping.app.presentation.base.BaseFragment


class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(R.layout.fragment_login) {
    override val viewModel: LoginViewModel by viewModels()


    override fun initView(savedInstanceState: Bundle?) {
        binding.apply {

        }
    }

}