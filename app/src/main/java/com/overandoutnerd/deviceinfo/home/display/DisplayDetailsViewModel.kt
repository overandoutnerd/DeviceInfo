package com.overandoutnerd.deviceinfo.home.display

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.home.ComponentDetailsState
import com.overandoutnerd.deviceinfo.home.UserDeviceDetailsProperty
import com.overandoutnerd.deviceinfo.home.device.DeviceDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DisplayDetailsViewModel(context: Context): ViewModel() {

    private val _displayDetailsState = mutableStateOf(ComponentDetailsState())
    val displayDetailsState: State<ComponentDetailsState> = _displayDetailsState

    private val repository = DisplayDetailsRepository(context)
    private var displayDetails by mutableStateOf<List<UserDeviceDetailsProperty>>(emptyList())

    init {
        getDetails()
    }

    private fun getDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                displayDetails = repository.getDetails()
                _displayDetailsState.value = _displayDetailsState.value.copy(
                    loading = false,
                    componentDetails = displayDetails
                )
            } catch (e: Exception) {
                _displayDetailsState.value = _displayDetailsState.value.copy(
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