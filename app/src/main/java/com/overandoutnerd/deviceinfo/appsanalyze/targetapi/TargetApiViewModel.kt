package com.overandoutnerd.deviceinfo.appsanalyze.targetapi

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

class TargetApiViewModel(context: Context): ViewModel() {

    private val _targetApiDetailsState = mutableStateOf(AnalyzeComponentsState<Int>())
    val targetApiDetailsState: State<AnalyzeComponentsState<Int>> = _targetApiDetailsState

    private val _appsState = mutableStateOf(AnalyzeAppsSheetState())
    val appsState: State<AnalyzeAppsSheetState> = _appsState


    private val repository = TargetApiRepository(context)
    private var targetApiDetails by mutableStateOf<List<AnalyzeComponent<Int>>>(emptyList())

    init {
        fetchDetails()
    }

    private fun fetchDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                targetApiDetails = repository.getDetails()
                _targetApiDetailsState.value = _targetApiDetailsState.value.copy(
                    loading = false,
                    componentItems = targetApiDetails
                )
            } catch(e: Exception) {
                _targetApiDetailsState.value = _targetApiDetailsState.value.copy(
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