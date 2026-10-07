
package com.overandoutnerd.deviceinfo.settings

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.overandoutnerd.deviceinfo.R
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

data class SettingsItem(
    val icon: Int,
    val title: String,
    val subTitle: String? = null,
    val flag: String
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val userSettings: UserSettings = UserSettingsImpl(context)

    val themeViewModel: ThemeViewModel = ThemeViewModel(context)

    private val _settingsItemsState = MutableStateFlow(SettingsState())
    val settingsItemsState: StateFlow<SettingsState> = _settingsItemsState

    private val _themeFlow = MutableStateFlow(themeViewModel.themeFlow.value)
    val themeFlow: StateFlow<AppTheme> = _themeFlow

    private val _theme = mutableStateOf(userSettings.theme)
    private val localeChangeReceiver = LocaleChangeReceiver {
        loadSettingsItems(context)
    }

    init {
        loadSettingsItems(context)
        viewModelScope.launch {
            themeViewModel.themeFlow.collect {
                Log.e("Settings View Model", "Inside collect Theme: $it")
                _themeFlow.value = it
                loadSettingsItems(context)
            }
        }
        context.registerReceiver(
            localeChangeReceiver,
            IntentFilter(Intent.ACTION_LOCALE_CHANGED)
        )
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(localeChangeReceiver)
    }

    fun updateTheme(theme: AppTheme) {
        Log.e("Settings View Model", "updateTheme called Theme: $theme")
        userSettings.theme = theme
    }

    fun loadSettingsItems(context: Context = getApplication()) {
        val currentLanguage = getCurrentLanguage(context)
        val appVersion = getAppVersion(context)
        val theme = context.getString(themeFlow.value.displayNameRes)
        Log.e("Settings View Model", "Inside loadSettingsItems Theme: $theme")

        val settingsItems = listOf(
            SettingsItem(
                icon = R.drawable.ic_light_mode_outlined,
                title = context.getString(R.string.settings_item_theme),
                subTitle = theme,
                flag = context.getString(R.string.settings_item_flag_theme)
            ),
            SettingsItem(
                icon = R.drawable.ic_language_outlined,
                title = context.getString(R.string.settings_item_language),
                subTitle = currentLanguage,
                flag = context.getString(R.string.settings_item_flag_language)
            ),
            SettingsItem(
                icon = R.drawable.ic_privacy_outlined,
                title = context.getString(R.string.settings_item_privacy_policy),
                flag = context.getString(R.string.settings_item_flag_privacy)
            ),
            SettingsItem(
                icon = R.drawable.ic_licenses_outlined,
                title = context.getString(R.string.settings_item_licenses),
                flag = context.getString(R.string.settings_item_flag_licenses)
            ),
            SettingsItem(
                icon = R.drawable.ic_empty_circle_outlined,
                title = context.getString(R.string.settings_item_app_version),
                subTitle = appVersion,
                flag = context.getString(R.string.settings_item_flag_version)
            )
        )
        Log.e("Settings View Model", "Settings Items, Theme Subtitle: ${settingsItems[0].subTitle}")

        _settingsItemsState.value = SettingsState(settingsItems)
        Log.e("Settings View Model", "Settings Items State, Theme Subtitle: ${_settingsItemsState.value.items[0].subTitle}")
    }

    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "Unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    private fun getCurrentLanguage(context: Context): String {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as android.app.LocaleManager
            if (localeManager.applicationLocales.isEmpty) {
                Locale.getDefault()
            }
            localeManager.applicationLocales[0] ?: Locale.getDefault()
        } else {
            context.resources.configuration.locales[0] ?: Locale.getDefault()
        }
        return locale.getDisplayName(locale)
    }

    fun openLanguageScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
                .apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context.startActivity(intent)
        } else {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context.startActivity(intent)
        }
    }

    fun getSelectedLanguageTag(): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return null
        val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as android.app.LocaleManager
        return if (localeManager.applicationLocales.isEmpty) null else localeManager.applicationLocales[0]?.language
    }

    fun supportsInAppLanguageSelection(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun setAppLanguage(tag: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as android.app.LocaleManager
            localeManager.applicationLocales = if (tag.isNullOrEmpty()) {
                android.os.LocaleList.getEmptyLocaleList()
            } else {
                android.os.LocaleList.forLanguageTags(tag)
            }
            loadSettingsItems(context)
        } else {
            openLanguageScreen()
        }
    }

    data class SettingsState(
        val items: List<SettingsItem> = emptyList()
    )
}

class LocaleChangeReceiver(private val onLocaleChanged: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_LOCALE_CHANGED) {
            onLocaleChanged()
        }
    }
}