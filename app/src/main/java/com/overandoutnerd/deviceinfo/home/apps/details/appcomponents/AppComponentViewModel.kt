package com.overandoutnerd.deviceinfo.home.apps.details.appcomponents

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class AppComponentViewModel(context: Context): ViewModel() {

    private val _viewState = mutableStateOf(ViewState())
    val viewState: State<ViewState> = _viewState

    private val repository = AppComponentRepository(context)
    private var components by mutableStateOf<List<SimpleComponent>>(emptyList())

    private fun fetchComponents(packageName: String, flag: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                components = repository.getComponents(packageName, flag)
                _viewState.value = _viewState.value.copy(
                    loading = false,
                    componentList = components
                )
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(
                    loading = false,
                    error = "Error Occurred: ${e.message}"
                )
            }

        }
    }

    fun loadComponents(packageName: String, flag: Int) {
        fetchComponents(packageName, flag)
    }

    fun updateState() {
        _viewState.value = ViewState()
    }

    data class ViewState(
        val loading: Boolean = true,
        val componentList: List<SimpleComponent> = emptyList(),
        val error: String? = null
    )

}