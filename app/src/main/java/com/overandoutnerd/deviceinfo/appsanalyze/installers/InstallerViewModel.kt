package com.overandoutnerd.deviceinfo.appsanalyze.installers

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeAppsSheetState
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeComponent
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeComponentsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InstallerViewModel(context: Context): ViewModel() {

    private val _installersDetailsState = mutableStateOf(AnalyzeComponentsState<String>())
    val installersDetailsState: State<AnalyzeComponentsState<String>> = _installersDetailsState

    private val _appsState = mutableStateOf(AnalyzeAppsSheetState())
    val appsState: State<AnalyzeAppsSheetState> = _appsState

    private val repository = InstallerRepository(context)
    private var installersDetails by mutableStateOf<List<AnalyzeComponent<String>>>(emptyList())

    init {
        fetchDetails()
    }

    private fun fetchDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                installersDetails = repository.getDetails()
                _installersDetailsState.value = _installersDetailsState.value.copy(
                    loading = false,
                    componentItems = installersDetails
                )
            } catch(e: Exception) {
                _installersDetailsState.value = _installersDetailsState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadDetails() {
        fetchDetails()
    }

}