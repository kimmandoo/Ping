package com.ping.app.ui.util

import android.content.Context
import android.widget.Toast
import kotlin.math.pow
import kotlin.math.roundToInt

fun round(number: Double, scale: Int): Double {
    val factor = 10.0.pow(scale.toDouble())
    return (number * factor).roundToInt() / factor
}

fun Context.easyToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}