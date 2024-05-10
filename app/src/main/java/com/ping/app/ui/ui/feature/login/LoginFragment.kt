package com.ping.app.ui.ui.feature.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.ping.app.R
import com.ping.app.data.repository.login.LoginRepoImpl
import com.ping.app.databinding.FragmentLoginBinding
import com.ping.app.ui.presentation.MainActivityViewModel
import com.ping.app.ui.presentation.login.LoginViewModel
import com.ping.app.ui.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val TAG = "LoginFragment_싸피"

/**
 * 본 프래그먼트는 로그인 기능을 하는 프래그먼트입니다.
 */
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(R.layout.fragment_login) {
    override val viewModel: LoginViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val loginRepoInstance = LoginRepoImpl.get()

    /**
     * 로그인 프래그먼트의 시작 부분입니다.
     * 해당 함수의 역할은 아래와 같습니다.
     * 1. 사용자 인증을 초기화
     * 2, 로그인 버튼 클릭시 signIn() 함수가 호출됩니다.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                googleSignIn(result.data)
            }
    }

    override fun initView(savedInstanceState: Bundle?) {
        loginRepoInstance.authInit(requireActivity())
        binding.apply {
            loginButton.setOnClickListener {
                googleSignInLauncher.launch(loginRepoInstance.getSignInIntent())
            }
            logoutButton.setOnClickListener {
                loginRepoInstance.logout()
                updateUI(null)
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
        }
    }

    /**
     * onActivityResult대신에 registerForActivityResult를 사용한 구글 로그인 구현
     * deprecated된 부분은 추후 수정
     */
    private fun googleSignIn(data: Intent?) {
        runCatching {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.getResult(ApiException::class.java)
        }.onSuccess { account ->
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id + "||" + account.idToken)
            CoroutineScope(Dispatchers.Main).launch {
                val user = loginRepoInstance.firebaseAuthWithGoogle(account.idToken!!)
                updateUI(user)
            }
        }.onFailure {
            Log.w(TAG, "Google sign in failed@@@@@@@@@", it)
        }
    }

    /**
     * 인증 여부에 따른 UI를 업데이트를 관리해주는 함수입니다.
     * 해당 함수에서 로그인한 유저의 테이블이 존재하는지 확인하고 존재 유무에 따라 테이블을 생성합니다.
     */
    private fun updateUI(user: FirebaseUser?) {
        binding.loginTv.text = if (user != null) {
            loginRepoInstance.userTableCheck(user)
            user.displayName.toString()
        } else {
            "인증 실패"
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
        }
    }
}