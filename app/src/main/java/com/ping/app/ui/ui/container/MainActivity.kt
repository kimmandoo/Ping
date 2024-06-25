package com.ping.app.ui.ui.container

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.ping.app.PingApplication
import com.ping.app.R
import com.ping.app.data.repository.login.LoginRepoImpl
import com.ping.app.databinding.ActivityMainBinding
import com.ping.app.ui.presentation.chat.ChatViewModel
import com.ping.app.ui.ui.feature.chat.ChatFragment
import com.ping.app.ui.ui.util.FCM
import com.ping.app.ui.ui.util.easyToast

private const val TAG = "MainActivity_싸피"

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var navController: NavController
    private lateinit var splashScreen: SplashScreen
    private val gptViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        PingApplication.loginRepo = LoginRepoImpl.initialize(this)
        LoginRepoImpl.getInstance().authInit()
        startAnimation()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        createNotificationChannel(FCM.CHANNEL_ID, FCM.CHANNEL_NAME)
        initView()
    }
    
    private fun startAnimation() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView.view,
                View.TRANSLATION_X,
                0f,
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 1000
            slideUp.doOnEnd { splashScreenView.remove() }
            slideUp.start()
        }
    }
    
    private fun initView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        navController = navHostFragment.navController
        binding.apply {
            mainGpt.setOnClickListener {
                val modal = ChatFragment()
                modal.show(supportFragmentManager, "modal")
            }
            navController.addOnDestinationChangedListener { _, destination, arguments ->
                when (destination.id) {
                    R.id.loginFragment -> {
                        gptViewModel.clearGpt()
                        mainGpt.visibility = View.GONE
                    }
                    
                    else -> {
                        mainGpt.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
    
    private fun initFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "FCM 토큰 얻기에 실패하였습니다.", task.exception)
                return@OnCompleteListener
            }
            
            Log.d(TAG, "token: ${task.result ?: "task.result is null"}")
        })
    }
    
    @SuppressLint("MissingPermission")
    private fun createNotificationChannel(id: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            TedPermission.create().setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    initFCM()
                    val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationManager.createNotificationChannel(
                            NotificationChannel(
                                id,
                                name,
                                NotificationManager.IMPORTANCE_HIGH
                            )
                        )
                    }
                }
                
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    easyToast("알림 권한이 거부되었습니다")
                }
                
            })
                .setDeniedMessage("알림 권한을 허용해주세요")
                .setPermissions(
                    android.Manifest.permission.POST_NOTIFICATIONS,
                )
                .check()
        }
    }
}