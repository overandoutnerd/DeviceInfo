package com.overandoutnerd.deviceinfo.home.sensors

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SensorDetailsViewModel : ViewModel() {
    private val _selectedSensor = mutableStateOf<SimpleSensorDetails?>(null)
    val selectedSensor: State<SimpleSensorDetails?> = _selectedSensor

    fun selectSensor(sensor: SimpleSensorDetails) {
        _selectedSensor.value = sensor
    }
}
