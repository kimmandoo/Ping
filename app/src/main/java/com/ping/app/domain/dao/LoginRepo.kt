package com.ping.app.domain.dao

import android.app.Activity
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface LoginRepo {
    fun getCurrentAuth(): FirebaseAuth?
    fun authInit(activity: Activity)

    fun logout()

    suspend fun firebaseAuthWithGoogle(idToken: String): FirebaseUser?

    suspend fun userTableCheckQuery(UID : String) : Boolean

    suspend fun createUserTable(user: FirebaseUser) : Boolean

    fun userMeetingGetQuery(UID : String)

    fun userTableCheck(user: FirebaseUser)
    fun getSignInIntent(): Intent
}