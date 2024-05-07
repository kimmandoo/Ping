package com.ping.app.presentation.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.ping.app.PingApplication
import com.ping.app.presentation.util.Map.FASTEST_UPDATE_INTERVAL

private const val TAG = "LocationHelper_싸피"

class LocationHelper private constructor(context: Context) {
    private var timeInterval: Long = FASTEST_UPDATE_INTERVAL.toLong()
    private var request: LocationRequest
    private var locationClient: FusedLocationProviderClient
    var currentLocation: Location? = null
    lateinit var listener: (Location) -> Unit
    
    init {
        // locationClient 획득하는 코드
        locationClient = LocationServices.getFusedLocationProviderClient(context)
        request = createRequest()
    }
    
    fun updateTimeInterval(timeInterval: Long) {
        this.timeInterval = timeInterval
        request = createRequest()
    }
    
    fun getClient():FusedLocationProviderClient{
        return locationClient
    }
    
    private fun createRequest(): LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval).apply {
            setMinUpdateDistanceMeters(10.0f)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
    
    
    fun startLocationTracking() {
        // 이걸 실행하기 전에 LOCATION관련 Permission 확인을 해야된다.
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                @SuppressLint("MissingPermission")
                override fun onPermissionGranted() {
                    locationClient.requestLocationUpdates(
                        request,
                        this@LocationHelper.LocationCallBack(),
                        null
                    )
                }
                
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(
                        PingApplication().applicationContext,
                        "위치 권한이 거부되었습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
            })
            .setDeniedMessage("위치 권한을 허용해주세요")
            .setPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            .check();
    }
    
    fun stopLocationTracking() {
        locationClient.flushLocations()
        locationClient.removeLocationUpdates(this.LocationCallBack())
    }
    
    
    inner class LocationCallBack() : LocationCallback() {
        override fun onLocationResult(location: LocationResult) {
            super.onLocationResult(location)
            val locationList = location.locations
            if (locationList.size > 0) {
                currentLocation = locationList[locationList.size - 1]
                currentLocation?.let {
                    listener(it)
                    Log.d(
                        TAG,
                        "onLocationResult: lng: ${it.longitude} lat: ${it.latitude}"
                    )
                }
            }
        }
        
        override fun onLocationAvailability(availability: LocationAvailability) {
            super.onLocationAvailability(availability)
        }
    }
    
    
    companion object {
        private var instance: LocationHelper? = null
        fun getInstance(context: Context): LocationHelper {
            if (instance == null) {
                synchronized(LocationHelper::class.java) {
                    if (instance == null) {
                        instance = LocationHelper(context)
                        Log.d(TAG, "getInstance: create with context")
                    }
                }
            }
            return instance!!
        }
        
        fun getInstance(): LocationHelper {
            return instance!!
        }
    }
}