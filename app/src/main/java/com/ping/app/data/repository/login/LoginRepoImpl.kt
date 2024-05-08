package com.ping.app.data.repository.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ping.app.R
import com.ping.app.domain.dao.LoginRepo
import com.ping.app.domain.model.User
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

private const val TAG = "LoginImpl_싸피"

class LoginRepoImpl(context: Context) : LoginRepo {
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun getCurrentAuth(): FirebaseAuth? {
        return if (::auth.isInitialized) auth else null
    }

    override fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    override fun logout() {
        auth.signOut()
        googleSignInClient.signOut()
    }

    /**
     * 해당 함수는 파이어베이스의 Authentication을 불러오는 기능을 하는 함수입니다.
     * 만일 "default_web_client_id"이 빨간색으로 뜰 경우
     * res 파일이 생성이 안된 문제입니다.
     * 해당 문제를 해결하기 위해서는 project/gradle의 google service의 버전을 조정해주면 됩니다.
     * 본 프로젝트에서는 "4.3.13"로 설정을 하였습니다.
     */
    override fun authInit(activity: Activity) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
        auth = Firebase.auth
    }

    /**
     * 해당 부분은 파이어 베이스의 Authentication에 접근하여 해당 유저의 데이터 값을 가져오는 함수로 추정돱니다.
     */
    override suspend fun firebaseAuthWithGoogle(idToken: String): FirebaseUser? {
        var user: FirebaseUser? = null
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        val getUser = CompletableDeferred<FirebaseUser?>()

        CoroutineScope(Dispatchers.IO).launch {
            auth.signInWithCredential(credential)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        user = auth.currentUser

                        Log.d(TAG, "firebaseAuthWithGoogle2: ${user}")
                        getUser.complete(user)
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    }
                }
        }

        getUser.await()

        Log.d(TAG, "firebaseAuthWithGoogle1: ${user?.uid}")
        return user
    }

    /**
     * 해당 함수에서는 유저 테이블을 학인하고 테이블이 존재한다면 메인으로 넘어가는 함수입니다.
     * 다만 해당 유저의 테이블이 존재하지 않는 다면 해당 유저의 테이블을 만들어 주는 역할을 하는 함수입니다.
     */
    override fun userTableCheck(user: FirebaseUser) {
//       테스트 용
//        CoroutineScope(Dispatchers.IO).launch {
//            createUserTable("1")
//        }

        CoroutineScope(Dispatchers.IO).launch {
            if (userTableCheckQuery(user.uid) == true) { // 해당 UID를 가지는 유저 테이블이 있을 경우
                // 테이블이 있다면 메인 프래그먼트로 넘어 감
                Log.d(TAG, "userTableCheck: user table mounted")

                userMeetingGetQuery(user.uid)


            } else { // 해당 UID를 가지는 유저 테이블이 없는 경우
                // 없을 경우 해당 테이블을 만들어야 함
                CoroutineScope(Dispatchers.IO).launch {
                    when (createUserTable(user)) {
                        true -> { // 유저 테이블 생성 완료
                            Log.d(TAG, "userTableCheck: 유저 테이블 생성을 완료했습니다.")
                        }

                        false -> { // 유저 테이블 생성 실패
                            Log.d(TAG, "userTableCheck: 유저 테이블 생성을 실패했습니다.")
                        }

                        else -> { // 유저 테이블 생성 실패
                            Log.d(TAG, "userTableCheck: 유저 테이블 생성을 실패했습니다.")
                        }
                    }
                }
            }
        }
    }


    /**테스트를 완료하지 않았습니다.
     *
     * 아래의 함수는 Firestore에 접근하여 값을 리턴 받는 함수 입니다.
     *
     * return -> true
     * UID에 해당하는 유저 테이블이 존재할 경우에는 true를 반환합니다.
     *
     * return -> false
     * UID에 해당하는 유저 테이블이 없을 경우와 디비 접근에 실패 했을 경우에는 false를 반환합니다.
     *
     */
    override suspend fun userTableCheckQuery(UID: String): Boolean {
        val getUserTable = CompletableDeferred<Task<DocumentSnapshot>>()
        val uidDocRef = db.collection("USER").document(UID)

        CoroutineScope(Dispatchers.IO).launch {
            val result = uidDocRef.get()
            getUserTable.complete(result)
        }

        val userTable = getUserTable.await()
        Log.d(TAG, "DocumentSnapshot data:${
            userTable.addOnSuccessListener {
                Log.d(TAG, "userTableCheckQuery: ${it.data}")
            }
        } "
        )

        return true
    }


    /**테스트를 완료하지 않았습니다.
     *
     * 아래의 함수는 만일 유저 테이블이 없을 경우 유저 테이블을 생성하는 함수 입니다.
     * 추후 UID 뿐만 아니라 USER 정보도 같이 input으로 들어옵니다.
     */
    override suspend fun createUserTable(user: FirebaseUser): Boolean {
        var result = false


        var inputUser: User = User(
            user.displayName!!,
            user.email!!,
            "",
            UUID.randomUUID().mostSignificantBits.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                db.collection("USER").document(user.uid)
                    .set(inputUser)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                result = true
            }
        }.join()

        Log.d(TAG, "createUserTable: ${result}")
        return result
    }


    /**
     * 본 함수는 USER UID로 Meeting Table의 정보를 가져 올 수 있는지 확인 하는 함수입니다.
     */
    override fun userMeetingGetQuery(UID: String) {
        val docRef = db.collection("MEETING").document(UID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    companion object {
        private var INSTANCE: LoginRepoImpl? = null

        fun initialize(context: Context): LoginRepoImpl {
            if (INSTANCE == null) {
                synchronized(LoginRepoImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = LoginRepoImpl(context)
                    }
                }
            }
            return INSTANCE!!
        }

        fun get(): LoginRepoImpl {
            return INSTANCE!!
        }
    }
}