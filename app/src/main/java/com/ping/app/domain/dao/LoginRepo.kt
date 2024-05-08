package com.ping.app.domain.dao

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface LoginRepo {
    fun getCurrentauth(): FirebaseAuth
    fun authinit(activity: Activity)

    suspend fun firebaseAuthWithGoogle(idToken: String): FirebaseUser?

    suspend fun userTableCheckQuery(UID : String) : Boolean

    suspend fun createUserTable(user: FirebaseUser) : Boolean

    fun userMeetingGetQuery(UID : String)

    fun userTableCheck(user: FirebaseUser)
}