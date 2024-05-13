package com.ping.app.ui.container

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.naver.maps.geometry.LatLng
import com.ping.app.PingApplication
import com.ping.app.R
import com.ping.app.databinding.ActivityMainBinding
import com.ping.app.ui.feature.map.PingMapViewModel
import com.ping.app.ui.util.LocationHelper

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var navController: NavController
    private val pingMapViewModel: PingMapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        PingApplication.locationHelper.startLocationTracking()
        PingApplication.locationHelper.listener = {
            pingMapViewModel.setUserLocation(LatLng(it))
        }
        initView()
    }
    
    private fun initView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        navController = navHostFragment.navController
        binding.apply {
            navController.addOnDestinationChangedListener { _, destination, arguments ->
                when (destination.id) {
                    R.id.loginFragment ->{
                        LocationHelper.getInstance().stopLocationTracking()
                    }
                    R.id.mainFragment ->{
                        LocationHelper.getInstance().stopLocationTracking()
                    }
                    R.id.pingAddMapFragment, R.id.pingMapFragment ->{
                        LocationHelper.getInstance()
                    }
                    
//                        View.INVISIBLE
//                    else ->  View.VISIBLE
                }
            }
        }
    }
}