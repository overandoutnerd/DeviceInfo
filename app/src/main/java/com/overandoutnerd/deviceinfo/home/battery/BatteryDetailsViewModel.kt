package com.overandoutnerd.deviceinfo.home.battery

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.home.ComponentDetailsState
import com.overandoutnerd.deviceinfo.home.UserDeviceDetailsProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BatteryDetailsViewModel(context: Context): ViewModel() {

    private val _batteryDetailsState = mutableStateOf(ComponentDetailsState())
    val batteryDetailsState: State<ComponentDetailsState> = _batteryDetailsState

    private val repository = BatteryDetailsRepository(context, this)
    private var batteryDetails by mutableStateOf<List<UserDeviceDetailsProperty>>(emptyList())

    private fun getDetails(batteryDetails: List<UserDeviceDetailsProperty>) {
        Log.e("BatteryDetailsViewModel", "getDetails() was called")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _batteryDetailsState.value = ComponentDetailsState(
                    loading = false,
                    componentDetails = batteryDetails
                )
                Log.e("BatteryDetailsViewModel", "Inside try block of getDetails(). ${batteryDetails[1].key}")
            } catch (e: Exception) {
                _batteryDetailsState.value = ComponentDetailsState(
                    loading = false,
                    error = e.message
                )
                Log.e("BatteryDetailsViewModel", "Error occurred: ${e.message}")
            }
        }
    }

    fun loadDetails(batteryDetails: List<UserDeviceDetailsProperty>) {
        Log.e("BatteryDetailsViewModel", "loadDetails() was called, calling getDetails()")
        getDetails(batteryDetails)
    }

}