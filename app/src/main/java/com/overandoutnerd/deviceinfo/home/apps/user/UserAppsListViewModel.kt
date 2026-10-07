package com.overandoutnerd.deviceinfo.home.apps.user

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.home.apps.SimpleAppDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserAppsListViewModel(context: Context): ViewModel() {

    private val _userAppsListState = mutableStateOf(UserAppsListState())
    var userAppsListState: State<UserAppsListState> = _userAppsListState

    private val repository = UserAppsListRepository(context)
    var userApps by mutableStateOf<List<SimpleAppDetails>>(emptyList())
        private set

    init {
        loadUserApps()
    }

    private fun loadUserApps(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userApps = repository.getUserInstalledApps()
                _userAppsListState.value = _userAppsListState.value.copy(
                    loading = false,
                    userApps = userApps,
                    error = null
                )
            } catch (e:Exception){
                _userAppsListState.value = _userAppsListState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    fun fetchUserApps(){
        loadUserApps()
    }

    data class UserAppsListState(
        var loading: Boolean = true,
        var userApps: List<SimpleAppDetails> = emptyList(),
        var error: String? = null
    )

}