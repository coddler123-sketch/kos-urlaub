package com.example.kosurlaub.ui.travel
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

fun isMarineCaution(weather: KosWeather?, marineWeather: MarineWeather?): Boolean =
    (weather?.windSpeedKmh ?: 0) >= 28 ||
        (marineWeather?.waveHeightMeters ?: 0.0) >= 1.2
