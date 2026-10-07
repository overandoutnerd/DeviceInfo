package com.overandoutnerd.deviceinfo.home.apps.system

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.home.apps.SimpleAppDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SystemAppsListViewModel(context: Context): ViewModel() {

    private val _systemAppsListState = mutableStateOf(SystemAppsListState())
    var systemAppsListState: State<SystemAppsListState> = _systemAppsListState

    private val repository = SystemAppsListRepository(context)
    var systemApps by mutableStateOf<List<SimpleAppDetails>>(emptyList())
        private set

    init {
        loadSystemApps()
    }

    private fun loadSystemApps(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                systemApps = repository.getSystemInstalledApps()
                _systemAppsListState.value = _systemAppsListState.value.copy(
                    loading = false,
                    systemApps = systemApps,
                    error = null
                )
            } catch (e:Exception){
                _systemAppsListState.value = _systemAppsListState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    fun fetchSystemApps(){
        loadSystemApps()
    }

    data class SystemAppsListState(
        var loading: Boolean = true,
        var systemApps: List<SimpleAppDetails> = emptyList(),
        var error: String? = null
    )

}