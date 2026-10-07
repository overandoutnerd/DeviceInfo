package com.overandoutnerd.deviceinfo.appsanalyze.signature

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeAppInfo
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeAppsSheetState
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeComponent
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeComponentsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignatureViewModel(context: Context): ViewModel() {

    private val _signaturesDetailsState = mutableStateOf(AnalyzeComponentsState<String>())
    val signaturesDetailsState: State<AnalyzeComponentsState<String>> = _signaturesDetailsState

    private val _appsState = mutableStateOf(AnalyzeAppsSheetState())
    val appsState: State<AnalyzeAppsSheetState> = _appsState

    private val repository = SignatureRepository(context)
    private var signaturesDetails by mutableStateOf<List<AnalyzeComponent<String>>>(emptyList())
    private var apps by mutableStateOf<List<AnalyzeAppInfo>>(emptyList())

    init {
        fetchDetails()
    }

    private fun fetchDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                signaturesDetails = repository.fetchDetails()
                _signaturesDetailsState.value = _signaturesDetailsState.value.copy(
                    loading = false,
                    componentItems = signaturesDetails
                )
            } catch(e: Exception) {
                _signaturesDetailsState.value = _signaturesDetailsState.value.copy(
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