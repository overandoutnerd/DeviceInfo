package com.overandoutnerd.deviceinfo.home.storage

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class StorageDetailsViewModel(context: Context): ViewModel() {

    private val _storageDetailsState = mutableStateOf(StorageDetailsState())
    val storageDetailsState: State<StorageDetailsState> = _storageDetailsState

    private val repository = StorageDetailsRepository(context)
    private var memoryInfoList by mutableStateOf<List<MemoryInfo>>(emptyList())
    private var storageDetails by mutableStateOf<List<StorageDetails?>>(emptyList())

    init {
        observeLiveStorageDetails()
    }

    private fun observeLiveStorageDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMemoryInfoListFlow(updateIntervalMs = 1000L)
                .catch { e ->
                    _storageDetailsState.value = _storageDetailsState.value.copy(
                        loading = false,
                        error = e.message
                    )
                }
                .collect { memoryList ->
                    memoryInfoList = memoryList
                    storageDetails = repository.getStorageDetails()
                    _storageDetailsState.value = _storageDetailsState.value.copy(
                        loading = false,
                        memoryInfoList = memoryInfoList,
                        storageDetails = storageDetails,
                        error = null
                    )
                }
        }
    }

    fun fetchStorageDetails() {
        observeLiveStorageDetails()
    }

    data class StorageDetailsState(
        var loading: Boolean = true,
        var memoryInfoList: List<MemoryInfo> = emptyList(),
        var storageDetails: List<StorageDetails?> = emptyList(),
        var error: String? = null
    )
}
