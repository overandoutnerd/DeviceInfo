package com.overandoutnerd.deviceinfo.appsanalyze

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toDrawable
import com.overandoutnerd.deviceinfo.R
data class AnalyzeComponentsState<T>(
    var loading: Boolean = true,
    var componentItems: List<AnalyzeComponent<T>> = emptyList(),
    var error: String? = null
)

data class AnalyzeAppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String?,
    val icon: Drawable
)

data class AnalyzeAppsSheetState(
    val visible: Boolean = false,
    val loading: Boolean = false,
    val title: String = "",
    val apps: List<AnalyzeAppInfo> = emptyList(),
    val error: String? = null
)

data class AnalyzeComponent<T>(
    val id: T,
    val title: String,
    val icon: Drawable = R.drawable.ic_android_filled.toDrawable(),
    val appCount: Int,
    val percentage: Float
)

enum class AnalyzeTab {
    ANALYZE_TAB_TARGET,
    ANALYZE_TAB_MINIMUM,
    ANALYZE_TAB_INSTALLERS,
    ANALYZE_TAB_SIGNATURES
}