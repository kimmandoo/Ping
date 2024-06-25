package com.ping.app.data.repository.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface LoginRepo {
    fun getCurrentAuth(): FirebaseAuth?
    fun authInit()

    suspend fun logout()

    suspend fun firebaseAuthWithGoogle(idToken: String): FirebaseUser?

    suspend fun checkUserTable(UID : String) : Boolean

    suspend fun createUserTable(user: FirebaseUser) : Boolean

    fun checkUserMeetingValidation(UID : String)

    fun checkUserTableCreated(user: FirebaseUser)
    suspend fun requestGoogleLogin(onSuccessListener: (FirebaseUser?) -> Unit)
    fun getUserInfo(): FirebaseUser?
    suspend fun setAccessToken(uid: String)
    suspend fun getAccessToken(): String
}