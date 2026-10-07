package com.overandoutnerd.deviceinfo.home.sensors

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SensorsViewModel(context: Context): ViewModel() {
    private val _sensorsState = mutableStateOf(SensorsState())
    var sensorsState: State<SensorsState> = _sensorsState

    private val repository = SensorsRepository(context)
    var sensors by mutableStateOf<List<SimpleSensorDetails>>(emptyList())
        private set

    init {
        loadSensors()
    }

    private fun loadSensors() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sensors = repository.getSensors()
                _sensorsState.value = sensorsState.value.copy(
                    loading = false,
                    sensors = sensors,
                    error = null
                )
            } catch (e: Exception) {
                _sensorsState.value = sensorsState.value.copy(
                    loading = false,
                    sensors = emptyList(),
                    error = e.message
                )
            }
        }
    }

    fun fetchSensors() {
        loadSensors()
    }

}

data class SensorsState(
    val loading: Boolean = true,
    val sensors: List<SimpleSensorDetails> = emptyList(),
    val error: String? = null
)