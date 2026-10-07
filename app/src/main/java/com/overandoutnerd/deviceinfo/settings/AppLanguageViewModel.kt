package com.overandoutnerd.deviceinfo.settings

import android.app.Application
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class AppLanguageViewModel(context: Context): ViewModel() {
    private val userSettings: UserSettings = UserSettingsImpl(context)

    private val _languagesState = mutableStateOf(LanguagesState())
    val languagesState: State<LanguagesState> = _languagesState

    private var languagesList by mutableStateOf<List<Locale>>(emptyList())
    init {
        getLanguages(context)
    }

    private fun getLanguages(context: Context) {
        viewModelScope.launch {
            try {
                languagesList = userSettings.loadLocales()
                _languagesState.value = _languagesState.value.copy(
                    loading = false,
                    languages = languagesList
                )
            } catch (e: Exception) {
                _languagesState.value = _languagesState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    data class LanguagesState(
        val loading: Boolean = true,
        val languages: List<Locale> = emptyList(),
        val error: String? = null
    )
}