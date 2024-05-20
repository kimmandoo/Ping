package com.ping.app.ui.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.ping.app.data.repository.login.LoginRepoImpl
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val loginRepoInstance = LoginRepoImpl.get()
    
    // update 고차함수 UI state로 이전시켜야됨
    suspend fun requestGoogleLogin(update: (FirebaseUser?) -> Unit) {
        loginRepoInstance.requestGoogleLogin { firebaseUser ->
            update(firebaseUser)
        }
    }
    
    fun getCurrentAuth(update: (FirebaseUser?) -> Unit) {
        viewModelScope.launch {
            loginRepoInstance.getCurrentAuth()?.let { auth ->
                auth.currentUser?.let {
                    loginRepoInstance.setAccessToken(it.uid)
                }
                update(auth.currentUser)
            }
        }
    }
    
    fun userTableCheck(user: FirebaseUser) {
        loginRepoInstance.userTableCheck(user)
    }
    
}