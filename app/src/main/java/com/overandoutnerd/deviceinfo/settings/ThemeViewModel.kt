package com.overandoutnerd.deviceinfo.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(context: Context): ViewModel() {
    private val userSettings: UserSettings = UserSettingsImpl(context)

    private val _themeFlow = MutableStateFlow(userSettings.theme)
    val themeFlow: StateFlow<AppTheme> = _themeFlow




    init {
        viewModelScope.launch {
            userSettings.themeStream.collect {
                Log.e("Theme View Model", "Inside collect Theme: $it")
                _themeFlow.value = it
            }
        }
    }
    fun updateTheme(theme: AppTheme) {
        Log.e("Theme View Model", "updateTheme called Theme: $theme")
        userSettings.theme = theme
    }
}

data class RadioButtonItem(
    val id: Int,
    val title: String
)