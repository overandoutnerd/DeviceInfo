package com.overandoutnerd.deviceinfo.settings

import android.app.LocaleManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import com.overandoutnerd.deviceinfo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

enum class AppTheme(val displayNameRes: Int) {
    MODE_SYSTEM(R.string.settings_theme_item_system),
    MODE_DAY(R.string.settings_theme_item_light),
    MODE_NIGHT(R.string.settings_theme_item_dark);

    companion object {
        fun fromOrdinal(ordinal: Int) = values()[ordinal]
    }
}

interface UserSettings {
    val themeStream: StateFlow<AppTheme>
    var theme: AppTheme

    suspend fun loadLocales(): List<Locale>
}

class UserSettingsImpl(private val context: Context): UserSettings {
    override val themeStream: MutableStateFlow<AppTheme>
    override var theme: AppTheme by AppThemePreferenceDelegate("app_theme", AppTheme.MODE_SYSTEM)

    private val preferences: SharedPreferences =
        context.getSharedPreferences("device_info", Context.MODE_PRIVATE)

    init {
        themeStream = MutableStateFlow(theme)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun loadLocales(): List<Locale> {
        return withContext(Dispatchers.IO) {
            val locales: MutableList<Locale> = mutableListOf()
            try {
                val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as LocaleManager
                val localesList: LocaleList = localeManager.applicationLocales

                for( i in 0 until localesList.size()) {
                    val defaultLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (localeManager.applicationLocales.isEmpty) {
                            Locale.getDefault()
                        }
                        localeManager.applicationLocales[0] ?: Locale.getDefault()
                    } else {
                        context.resources.configuration.locales[0] ?: Locale.getDefault()
                    }
                    locales.add(defaultLocale)
                    localesList.get(i).let {
                        locales.add(it)
                        Log.e("User Settings", "Language: ${it.displayName}, Code: ${it.language}")
                    }
                }

                locales
            } catch (e: Exception) {
                Log.e("User Settings", "${e.message}")
                locales
            }
        }
    }

    inner class AppThemePreferenceDelegate(
        private val name: String,
        private val default: AppTheme
    ): ReadWriteProperty<Any?, AppTheme> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): AppTheme {
            val value = AppTheme.fromOrdinal(preferences.getInt(name, default.ordinal))
            Log.e("User Settings", "getValue() called: $value")
            return value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: AppTheme) {
            preferences.edit { putInt(name, value.ordinal) }
            themeStream.value = value
            Log.e("User Settings", "setValue() called: $value")
        }
    }

}