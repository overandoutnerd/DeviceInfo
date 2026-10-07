package com.overandoutnerd.deviceinfo.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.overandoutnerd.deviceinfo.R

data class BottomNavigationItem(
    @StringRes val navBarTitle: Int,
    @StringRes val appBarTitle: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int
)

class MainViewModel(private val savedStateHandle: SavedStateHandle): ViewModel() {

    private val bottomNavigationItems = listOf(
        BottomNavigationItem(
            R.string.nav_item_title_home,
            R.string.nav_item_app_bar_title_home,
            R.drawable.ic_home_filled,
            R.drawable.ic_home_outlined
        ),
        BottomNavigationItem(
            R.string.nav_item_title_analyze,
            R.string.nav_item_app_bar_title_analyze,
            R.drawable.ic_build_filled,
            R.drawable.ic_build_outlined
        ),
//        BottomNavigationItem(
//            R.string.nav_item_title_wellbeing,
//            R.string.nav_item_app_bar_title_wellbeing,
//            R.drawable.ic_digital_wellbeing_filled,
//            R.drawable.ic_digital_wellbeing_outlined
//        ),
        BottomNavigationItem(
            R.string.nav_item_title_settings,
            R.string.nav_item_app_bar_title_settings,
            R.drawable.ic_settings_filled,
            R.drawable.ic_settings_outlined
        )
    )
    private val _bottomNavigationItemsState = mutableStateOf(bottomNavigationItems)
    val bottomNavigationItemsState: State<List<BottomNavigationItem>> = _bottomNavigationItemsState

    private val _selectedNavItem = mutableIntStateOf(savedStateHandle["selectedNavItem"] ?: 0)
    val selectedNavItem: State<Int> = _selectedNavItem

    private val _logo = mutableIntStateOf(R.drawable.device_info_logo_wide)
    val logo: State<Int> = _logo

    private val _showSplashScreen = mutableStateOf(true)
    val showSplashScreen: State<Boolean> = _showSplashScreen

    fun dismissSplashScreen() {
        _showSplashScreen.value = false
    }

    fun setSelectedNavItem(index: Int) {
        _selectedNavItem.intValue = index
        savedStateHandle["selectedNavItem"] = index
    }
}
