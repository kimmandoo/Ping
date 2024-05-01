package com.ping.app.presentation.ui.feature.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ping.app.R
import com.ping.app.databinding.FragmentLoginBinding
import com.ping.app.presentation.base.BaseFragment


private const val TAG = "LoginFragment_싸피"

/**
 * 본 프래그먼트는 로그인 기능을 하는 프래그먼트입니다.
 *
 * 해야 하는 것
 * google-service.json을 세팅
 *
 *      id("com.android.application") version "8.2.2" apply false
 *     id("org.jetbrains.kotlin.android") version "1.9.22" apply false
 *     id("com.google.gms.google-services") version "4.3.13" apply false
 *
 *
 * 테스트 환경 gradle 버전
 *      implementation("androidx.core:core-ktx:1.12.0")
 *      implementation("androidx.appcompat:appcompat:1.6.1")
 *      implementation("com.google.android.material:material:1.11.0")
 *      implementation("androidx.constraintlayout:constraintlayout:2.1.4")
 *      testImplementation("junit:junit:4.13.2")
 *      androidTestImplementation("androidx.test.ext:junit:1.1.5")
 *      androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
 * //    implementation ("androidx.navigation:navigation-fragment-ktx:$nav_version")
 * //    implementation ("androidx.navigation:navigation-ui-ktx:$nav_version")
 *
 *
 *      implementation ("com.google.android.gms:play-services-maps:18.0.2")
 *      implementation ("com.google.android.gms:play-services-location:20.0.0")
 *
 *     // firebase 사용에 필요한 의존성 추가 firebase + database
 *     implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
 *     implementation("com.google.firebase:firebase-database-ktx")
 *
 *     // firebase auth 에서 필요한 의존성 추가
 *     implementation ("com.google.firebase:firebase-auth-ktx")
 *     implementation ("com.google.android.gms:play-services-auth:20.7.0")
 *
 *     implementation ("com.github.bumptech.glide:glide:4.12.0")
 *
 */

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(R.layout.fragment_login) {
    override val viewModel: LoginViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    /**
     * 로그인 프래그먼트의 시작 부분입니다.
     * 해당 함수의 역할은 아래와 같습니다.
     * 1. 사용자 인증을 초기화
     * 2, 로그인 버튼 클릭시 signIn() 함수가 호출됩니다.
     */
    override fun initView(savedInstanceState: Bundle?) {

//        authinit()

        binding.apply {
            binding.loginButton.setOnClickListener {
//                signIn()
            }
        }
    }

    /**
     * 해당 함수는 파이어베이스의 Authentication을 불러오는 기능을 하는 함수입니다.
     * 만일 "default_web_client_id"이 빨간색으로 뜰 경우
     * res 파일이 생성이 않된 문제입니다.
     * 해당 문제를 해결하기 위해서는 project/gradle의 google service의 버전을 조정해주면 됩니다.
     * 본 프로젝트에서는 "4.3.13"로 설정을 하였습니다.
     */
//    private fun authinit(){
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//        auth = Firebase.auth
//
//    }

//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
//    }

    // [START onactivityresult]
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)!!
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed@@@@@@@@@", e)
//            }
//        }
//    }
    // [END onactivityresult]

    // [START auth_with_google]
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    updateUI(null)
//                }
//            }
//    }
    // [END auth_with_google]

    // [START signin]
//    private fun signIn() {
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }
    // [END signin]

    // 인증 성공 여부에 따른 화면 처리
//    private fun updateUI(user: FirebaseUser?) {
//        if (user != null) {
//
//            binding.loginTv.setText(user.displayName.toString())
//
//        } else {
//            binding.loginTv.setText.setText("인증 실패")
//        }
//    }

//    companion object {
//        private const val RC_SIGN_IN = 9001
//    }

}