package com.overandoutnerd.deviceinfo

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(var route: String) {
    object DeviceInfoScreen: Screen("deviceinfoscreen")
    object DeviceInfoHomeScreen: Screen("deviceinfohomescreen")
    object InstalledAppsScreen: Screen("installedappsscreen")
    object AppDetails: Screen("appdetails")
    object AppPermissionsScreen: Screen("apppermissionsscreen")
    object ThemeChooserDialog: Screen("themechooserdialog")
    object SensorDetails: Screen("sensordetails")
    object LanguageScreen: Screen("languagescreen")
}

sealed class MainNavigationScreen(
    var route: String,
    @StringRes var appBarTitle: Int,
    @StringRes var navBarTitle: Int,
    @DrawableRes var selectedIcon: Int,
    @DrawableRes var unselectedIcon: Int
) {
    data object HomeScreen: MainNavigationScreen(
        "home",
        R.string.nav_item_app_bar_title_home,
        R.string.nav_item_title_home,
        R.drawable.ic_home_filled,
        R.drawable.ic_home_filled
    )
    data object AnalyzeScreen: MainNavigationScreen(
        "analyze",
        R.string.nav_item_app_bar_title_analyze,
        R.string.nav_item_title_analyze,
        R.drawable.ic_build_filled,
        R.drawable.ic_build_outlined
    )
    data object WellbeingScreen: MainNavigationScreen(
        "wellbeing",
        R.string.nav_item_app_bar_title_wellbeing,
        R.string.nav_item_title_wellbeing,
        R.drawable.ic_digital_wellbeing_filled,
        R.drawable.ic_digital_wellbeing_outlined
    )
    data object SettingsScreen: MainNavigationScreen(
        "settings",
        R.string.nav_item_app_bar_title_settings,
        R.string.nav_item_title_settings,
        R.drawable.ic_account_circle_filled,
        R.drawable.ic_account_circle_outlined
    )
}