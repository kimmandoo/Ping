package com.ping.app.ui.ui.feature.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.ping.app.R
import com.ping.app.data.repository.login.LoginRepoImpl
import com.ping.app.databinding.FragmentLoginBinding
import com.ping.app.ui.base.BaseFragment
import com.ping.app.ui.presentation.login.LoginViewModel
import com.ping.app.ui.ui.util.easyToast
import kotlinx.coroutines.launch


private const val TAG = "LoginFragment_싸피"

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(R.layout.fragment_login) {
    override val viewModel: LoginViewModel by viewModels()
    private val loginRepoInstance = LoginRepoImpl.get()

    override fun initView(savedInstanceState: Bundle?) {
        binding.apply {
            loginBtnGoogleLogin.setOnClickListener {
                lifecycleScope.launch {
                    loginRepoInstance.requestGoogleLogin(binding.root.context) { firebaseUser ->
                        updateUI(firebaseUser)
                    }
                }
            }
        }
    }
    
    /**
     * 자동로그인
     */
    override fun onStart() {
        super.onStart()
        loginRepoInstance.getCurrentAuth()?.let { auth ->
            lifecycleScope.launch {
                auth.currentUser?.let {
                    Log.d(TAG, "onStart: ${it.uid}")
                    loginRepoInstance.setAccessToken(it.uid)
                }
            }
            updateUI(auth.currentUser)
            Log.d(TAG, "onStart:${auth.currentUser} ")
        }
    }

    /**
     * 인증 여부에 따른 UI를 업데이트를 관리해주는 함수
     * 해당 함수에서 로그인한 유저의 테이블이 존재하는지 확인하고 존재 유무에 따라 테이블을 생성합니다.
     */
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            loginRepoInstance.userTableCheck(user)
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
        } else {
            binding.root.context.easyToast(getString(R.string.login_failed))
        }
    }
}