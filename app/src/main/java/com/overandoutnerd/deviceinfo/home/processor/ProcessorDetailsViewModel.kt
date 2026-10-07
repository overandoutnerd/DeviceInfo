package com.overandoutnerd.deviceinfo.home.processor

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

class ProcessorDetailsViewModel(context: Context): ViewModel() {

    private val _processorDetailsState = mutableStateOf(ComponentDetailsState())
    val processorDetailsState: State<ComponentDetailsState> = _processorDetailsState

    private val repository = ProcessorDetailsRepository(context)
    private var processorDetails by mutableStateOf<List<UserDeviceDetailsProperty>>(emptyList())

    init {
        getDetails()
    }

    private fun getDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                processorDetails = repository.getDetails()
                _processorDetailsState.value = _processorDetailsState.value.copy(
                    loading = false,
                    componentDetails = processorDetails
                )
            } catch (e: Exception) {
                _processorDetailsState.value = _processorDetailsState.value.copy(
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