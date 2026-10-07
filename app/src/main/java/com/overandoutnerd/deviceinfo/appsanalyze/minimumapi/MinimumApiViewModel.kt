package com.overandoutnerd.deviceinfo.appsanalyze.minimumapi

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

class MinimumApiViewModel(context: Context): ViewModel() {

    private val _minApiDetailsState = mutableStateOf(AnalyzeComponentsState<Int>())
    val minApiDetailsState: State<AnalyzeComponentsState<Int>> = _minApiDetailsState

    private val _appsState = mutableStateOf(AnalyzeAppsSheetState())
    val appsState: State<AnalyzeAppsSheetState> = _appsState


    private val repository = MinimumApiRepository(context)
    private var minimumApiDetails by mutableStateOf<List<AnalyzeComponent<Int>>>(emptyList())

    init {
        fetchDetails()
    }

    private fun fetchDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                minimumApiDetails = repository.getDetails()
                _minApiDetailsState.value = _minApiDetailsState.value.copy(
                    loading = false,
                    componentItems = minimumApiDetails
                )
            } catch(e: Exception) {
                _minApiDetailsState.value = _minApiDetailsState.value.copy(
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