package com.overandoutnerd.deviceinfo.digitalwellbeing

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScreenUsageViewModel(context: Context): ViewModel() {

    private val _screenUsageState = mutableStateOf(ScreenUsageState())
    val screenUsageState: State<ScreenUsageState> = _screenUsageState

    private val repository = ScreenUsageRepository(context)
    private var screenUsage by mutableStateOf<DailyUsage>(DailyUsage("", 0, "", emptyList()))
    private var weekUsage by mutableStateOf<WeeklyUsage>(WeeklyUsage(0, "", emptyList()))

    private val _usagePermissionImage = mutableIntStateOf(R.drawable.digital_wellbeing_home_banner)
    val usagePermissionImage: State<Int> = _usagePermissionImage

    private val _usagePermissionTitle = mutableStateOf(context.getString(R.string.usage_permission_screen_title))
    val usagePermissionTitle: State<String> = _usagePermissionTitle

    private val _usagePermissionDescription = mutableStateOf(context.getString(R.string.usage_permission_screen_description))
    val usagePermissionDescription: State<String> = _usagePermissionDescription


    private val _usagePermissionPrivacyInfo = mutableStateOf(context.getString(R.string.usage_permission_screen_privacy_info))
    val usagePermissionPrivacyInfo: State<String> = _usagePermissionPrivacyInfo

    private val _usagePermissionButtonTitle = mutableStateOf(context.getString(R.string.usage_permission_screen_button_title))
    val usagePermissionButtonTitle: State<String> = _usagePermissionButtonTitle

    init {
        loadScreenUsageDetails()
    }

    private fun loadScreenUsageDetails(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                weekUsage = repository.getWeeklyUsage()
                screenUsage = repository.getMostUsedApps()
                _screenUsageState.value = _screenUsageState.value.copy(
                    loading = false,
                    screenUsage = screenUsage,
                    error = null
                )
            } catch (e: Exception) {
                _screenUsageState.value = _screenUsageState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    fun fetchScreenUsageDetails(){
        loadScreenUsageDetails()
    }

    data class ScreenUsageState(
        var loading: Boolean = true,
        var screenUsage: DailyUsage = DailyUsage("", 0, "", emptyList()),
        var error: String? = null
    )

}