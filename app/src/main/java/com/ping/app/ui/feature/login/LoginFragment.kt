package com.ping.app.ui.feature.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.ping.app.R
import com.ping.app.data.repository.login.LoginRepoImpl
import com.ping.app.databinding.FragmentLoginBinding
import com.ping.app.presentation.ui.container.MainActivityViewModel
import com.ping.app.presentation.ui.feature.login.LoginViewModel
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


    /**
     * 로그인 프래그먼트의 시작 부분입니다.
     * 해당 함수의 역할은 아래와 같습니다.
     * 1. 사용자 인증을 초기화
     * 2, 로그인 버튼 클릭시 signIn() 함수가 호출됩니다.
     */
    override fun initView(savedInstanceState: Bundle?) {

        LoginRepoImpl.get().authinit(requireActivity())

        binding.apply {
            test2.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
            }
            test.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_pingAddMapFragment)
            }
            binding.loginButton.setOnClickListener {
                signIn()
            }
        }


    }


    override fun onStart() {
        super.onStart()
        val currentUser = LoginRepoImpl.get().getCurrentauth().currentUser
        updateUI(currentUser)
        getUserUid(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account)
                CoroutineScope(Dispatchers.Main).launch {
                    val user = LoginRepoImpl.get().firebaseAuthWithGoogle(account.idToken!!)
                    updateUI(user)
                }

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed@@@@@@@@@", e)
            }
        }
    }

    private fun signIn() {
        val signInIntent = LoginRepoImpl.get().googleSignInClient.signInIntent
        Log.d(TAG, "signIn: ${signInIntent}")
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    /**
     * 인증 여부에 따른 UI를 업데이트를 관리해주는 함수입니다.
     * 해당 함수에서 로그인한 유저의 테이블이 존재하는지 확인하고 존재 유무에 따라 테이블을 생성합니다.
     */
    fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            binding.loginTv.setText(user.displayName.toString())
            LoginRepoImpl.get().userTableCheck(user)
        } else {
            binding.loginTv.setText("인증 실패")
        }
    }

    /**
     * 해당 함수는 유저의 Uid를 MainActivity의 Viewmodel에 저장하는 기능을 가진 함수입니다.
     * mainViewmodel에 넣은 이유는 user의 uid가 메인 액티비티가 살아있는 동안은 계속
     * 유지가 되어야 한다고 생각을 했기 때문입니다.
     */
    fun getUserUid(user: FirebaseUser?){
        if (user != null) {
            mainActivityViewModel.saveUserUid(user.uid)
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

}