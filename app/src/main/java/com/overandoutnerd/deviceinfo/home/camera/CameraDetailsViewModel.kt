package com.overandoutnerd.deviceinfo.home.camera

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.home.UserDeviceDetailsProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CameraDetailsViewModel(context: Context): ViewModel() {

    private val _cameraDetailsState = mutableStateOf(CameraDetailsState())
    val cameraDetailsState: State<CameraDetailsState> = _cameraDetailsState

    val repository = CameraDetailsRepository(context)
    private var cameraDetails by mutableStateOf<List<List<UserDeviceDetailsProperty>>>(emptyList())

    init {
        fetchCameraDetails()
    }

    private fun fetchCameraDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cameraDetails = repository.getCameraDetails()
                _cameraDetailsState.value = _cameraDetailsState.value.copy(
                    loading = false,
                    cameraDetails = cameraDetails,
                    error = null
                )
            } catch(e: Exception) {
                _cameraDetailsState.value = _cameraDetailsState.value.copy(
                    loading = false,
                    error = "${e.message}"
                )
            }
        }
    }

    fun loadCameraDetails(){
        fetchCameraDetails()
    }

    data class CameraDetailsState(
        val loading: Boolean = true,
        val cameraDetails: List<List<UserDeviceDetailsProperty>> = emptyList(),
        val error: String? = null
    )
}