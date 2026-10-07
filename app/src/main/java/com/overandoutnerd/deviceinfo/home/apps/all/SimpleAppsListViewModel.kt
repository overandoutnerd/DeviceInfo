package com.overandoutnerd.deviceinfo.home.apps.all

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

class SimpleAppsListViewModel(context: Context): ViewModel() {

    private val _simpleAppsListState = mutableStateOf(SimpleAppsListState())
    var simpleAppsListState: State<SimpleAppsListState> = _simpleAppsListState

    private val repository = SimpleAppsListRepository(context)
    var simpleApps by mutableStateOf<List<SimpleAppDetails>>(emptyList())
        private set

    init {
        loadSimpleApps()
    }

    private fun loadSimpleApps(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                simpleApps = repository.getInstalledApps()
                _simpleAppsListState.value = _simpleAppsListState.value.copy(
                    loading = false,
                    simpleApps = simpleApps,
                    error = null
                )
            } catch (e:Exception){
                _simpleAppsListState.value = _simpleAppsListState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    fun fetchSimpleApps(){
        loadSimpleApps()
    }

    data class SimpleAppsListState(
        var loading: Boolean = true,
        var simpleApps: List<SimpleAppDetails> = emptyList(),
        var error: String? = null
    )
}