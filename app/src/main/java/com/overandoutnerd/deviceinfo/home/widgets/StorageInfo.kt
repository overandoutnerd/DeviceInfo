package com.overandoutnerd.deviceinfo.home.widgets

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

sealed interface StorageInfo {

    object Loading: StorageInfo

    data class Available(
        val total: String = "",
        val free: String = "",
        val progress: Float = 0f,
        val value: String = ""
    ): StorageInfo

    data class Unavailable(
        val message: String
    ): StorageInfo

    companion object {
        val totalKey = stringPreferencesKey("total")
        val freeKey = stringPreferencesKey("free")
        val progressKey = floatPreferencesKey("progress")
        val valueKey = stringPreferencesKey("value")
        val statusKey = stringPreferencesKey("status")
        val messageKey = stringPreferencesKey("message")
    }

}