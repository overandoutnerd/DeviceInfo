package com.overandoutnerd.deviceinfo.home.device

import android.content.Context
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

class DeviceDetailsViewModel(context: Context): ViewModel() {

    private val _deviceDetailsState = mutableStateOf(ComponentDetailsState())
    val deviceDetailsState: State<ComponentDetailsState> = _deviceDetailsState

    private val repository = DeviceDetailsRepository(context)
    private var deviceDetails by mutableStateOf<List<UserDeviceDetailsProperty>>(emptyList())

    init {
        getDetails()
    }

    private fun getDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deviceDetails = repository.getDetails()
                _deviceDetailsState.value = _deviceDetailsState.value.copy(
                    loading = false,
                    componentDetails = deviceDetails
                )
            } catch (e: Exception) {
                _deviceDetailsState.value = _deviceDetailsState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadDetails() {
        getDetails()
    }

}