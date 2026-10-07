package com.overandoutnerd.deviceinfo.home.apps.details

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppDetailsViewModel(context: Context): ViewModel() {

    private val _appDetailsState = mutableStateOf(AppDetailsState())
    var appDetailsState: State<AppDetailsState> = _appDetailsState

    private val repository = AppDetailsRepository(context)
    private var appDetails by mutableStateOf<AppDetails?>(null)
        private set

    private fun loadAppDetails(packageName: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                appDetails = repository.getAppDetails(packageName)
                _appDetailsState.value = _appDetailsState.value.copy(
                    loading = false,
                    appDetails = appDetails,
                    error = null
                )
            } catch (e:Exception){
                _appDetailsState.value = _appDetailsState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    fun fetchAppDetails(packageName: String){
        loadAppDetails(packageName)
    }

    fun updateState(){
        _appDetailsState.value = AppDetailsState()
    }

    data class AppDetailsState(
        var loading: Boolean = true,
        var appDetails: AppDetails? = null,
        var error: String? = null
    )

}