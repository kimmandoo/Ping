package com.ping.app.presentation.util

import kotlin.math.pow
import kotlin.math.roundToInt

fun round(number: Double, scale: Int): Double {
    val factor = 10.0.pow(scale.toDouble())
    return (number * factor).roundToInt() / factor
}