package com.ping.app.presentation.ui.container

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.naver.maps.geometry.LatLng
import com.ping.app.R
import com.ping.app.presentation.util.LocationHelper
import com.ping.app.databinding.ActivityMainBinding
import com.ping.app.presentation.ui.feature.map.MapViewModel

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var navController: NavController
    private val mapViewModel: MapViewModel by viewModels()
    private val locationHelperInstance by lazy {
        LocationHelper.getInstance(this)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        locationHelperInstance.startLocationTracking()
        locationHelperInstance.listener = {
            mapViewModel.setUserLocation(LatLng(it))
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

                    }
                    R.id.mainFragment ->{
                        locationHelperInstance.startLocationTracking()
                    }
//                        View.INVISIBLE
//                    else ->  View.VISIBLE
                }
            }
        }
    }
}