package com.overandoutnerd.deviceinfo.home.system

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

class SystemDetailsViewModel(context: Context): ViewModel() {

    private val _systemDetailsState = mutableStateOf(ComponentDetailsState())
    val systemDetailsState: State<ComponentDetailsState> = _systemDetailsState

    private val repository = SystemDetailsRepository(context)
    private var systemDetails by mutableStateOf<List<UserDeviceDetailsProperty>>(emptyList())

    init {
        getDetails()
    }

    private fun getDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                systemDetails = repository.getDetails()
                _systemDetailsState.value = _systemDetailsState.value.copy(
                    loading = false,
                    componentDetails = systemDetails
                )
            } catch (e: Exception) {
                _systemDetailsState.value = _systemDetailsState.value.copy(
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