package com.ping.app.presentation.util

import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt

fun round(number: Double, scale: Int): Double {
    val factor = 10.0.pow(scale.toDouble())
    return (number * factor).roundToInt() / factor
}

fun Fragment.getAddress(lat: Double, lng: Double): String {
    val geoCoder = Geocoder(requireContext(), Locale.KOREA)
    var addressResult = "주소를 가져 올 수 없습니다."
    runCatching {
        geoCoder.getFromLocation(lat, lng, 1) as ArrayList<Address>
    }.onSuccess { response ->
        if (response.size > 0) {
            val currentLocationAddress = response[0].getAddressLine(0)
                .toString()
            addressResult = currentLocationAddress
        }
    }.onFailure {
        Log.d("getAddress", "error: ${it.stackTrace}")
    }
    
    return addressResult
}