package com.ping.app.ui.ui.feature.login

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.ping.app.R
import com.ping.app.data.repository.login.LoginRepoImpl
import com.ping.app.databinding.FragmentLoginBinding
import com.ping.app.ui.base.BaseFragment
import com.ping.app.ui.presentation.MainActivityViewModel
import com.ping.app.ui.presentation.login.LoginViewModel
import com.ping.app.ui.ui.util.easyToast
import kotlinx.coroutines.launch
import org.w3c.dom.Text


private const val TAG = "LoginFragment_싸피"

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(R.layout.fragment_login) {
    override val viewModel: LoginViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
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
     * onStart시에 user상태를 확인해서 자동로그인을 해주는 로직
     */
    override fun onStart() {
        super.onStart()
        loginRepoInstance.getCurrentAuth()?.let { auth ->
            updateUI(auth.currentUser)
            getUserUid(auth.currentUser)
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

    /**
     * 해당 함수는 유저의 Uid를 MainActivity의 Viewmodel에 저장하는 기능을 가진 함수입니다.
     * mainViewmodel에 넣은 이유는 user의 uid가 메인 액티비티가 살아있는 동안은 계속
     * 유지가 되어야 한다고 생각을 했기 때문입니다.
     */
    private fun getUserUid(user: FirebaseUser?) {
        if (user != null) {
            mainActivityViewModel.saveUserUid(user.uid)
            Log.d(TAG, "getUserUid: ${mainActivityViewModel.userUid.value}")
        }
    }
}