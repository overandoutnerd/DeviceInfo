package com.overandoutnerd.deviceinfo

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.overandoutnerd.deviceinfo.home.ContextViewModelFactory
import com.overandoutnerd.deviceinfo.home.apps.details.appcomponents.AppComponentViewModel
import com.overandoutnerd.deviceinfo.home.apps.details.AppInfoScreen
import com.overandoutnerd.deviceinfo.home.apps.details.AppDetailsViewModel
import com.overandoutnerd.deviceinfo.home.apps.details.appcomponents.AppComponentScreen
import com.overandoutnerd.deviceinfo.home.sensors.SensorDetailsScreen
import com.overandoutnerd.deviceinfo.home.sensors.SensorDetailsViewModel
import com.overandoutnerd.deviceinfo.settings.AppTheme
import com.overandoutnerd.deviceinfo.settings.LanguageSelectionScreen
import com.overandoutnerd.deviceinfo.settings.SettingsViewModel
import com.overandoutnerd.deviceinfo.settings.ThemeViewModel
import com.overandoutnerd.deviceinfo.ui.DeviceInfoScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeviceInfoApp(
    context: Context,
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    themeViewModel: ThemeViewModel,
    selectedTheme: AppTheme,
    hasUsageStatsPermission: (Boolean) -> Boolean,
    modifier: Modifier = Modifier
) {
    val appDetailsViewModel: AppDetailsViewModel = viewModel(factory = ContextViewModelFactory(context))
    val appComponentViewModel: AppComponentViewModel = viewModel(factory = ContextViewModelFactory(context))
    val sensorDetailsViewModel: SensorDetailsViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = Screen.DeviceInfoScreen.route,
        modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)
    ){
        composable(route = Screen.DeviceInfoScreen.route){
            DeviceInfoScreen(
                context = context,
                settingsViewModel = settingsViewModel,
                themeViewModel = themeViewModel,
                selectedTheme = selectedTheme,
                navigateToAppDetails = {
                    appDetailsViewModel.viewModelScope.launch(Dispatchers.IO) {
                        appDetailsViewModel.fetchAppDetails(it.packageName)
                    }
                    navController.navigate(Screen.AppDetails.route)
                },
                navigateToSensorDetails = {
                    sensorDetailsViewModel.selectSensor(it)
                    navController.navigate(Screen.SensorDetails.route)
                },
                hasUsageStatsPermission = hasUsageStatsPermission,
                onClickSettingItem = { flag ->
                    when(flag) {
                        context.getString(R.string.settings_item_flag_language) -> { navController.navigate(Screen.LanguageScreen.route) }
                        else -> { }
                    }
                }
            )
        }
        composable(route = Screen.AppDetails.route){
            AppInfoScreen(
                viewModel = appDetailsViewModel,
                onBackPressed = {
                    navController.navigateUp()
                    appDetailsViewModel.updateState()
                },
                navigateToComponents = { title, packageName, flag ->
                    appComponentViewModel.viewModelScope.launch(Dispatchers.IO) {
                        appComponentViewModel.loadComponents(packageName, flag)
                    }
                    navController.currentBackStackEntry?.savedStateHandle?.set("title", title)
                    navController.navigate(Screen.AppPermissionsScreen.route)
                }
            )
        }
        composable(route = Screen.AppPermissionsScreen.route){
            val title = navController.previousBackStackEntry?.savedStateHandle?.
                get<String>("title") ?: ""
            AppComponentScreen(
                viewModel = appComponentViewModel,
                componentTitle = title,
                onBackPressed = {
                    navController.navigateUp()
                    appComponentViewModel.updateState()
                }
            )
        }
        composable(route = Screen.SensorDetails.route) {
            SensorDetailsScreen(
                sensor = sensorDetailsViewModel.selectedSensor.value,
                onBackPressed = { navController.navigateUp() }
            )
        }
        composable(route = Screen.LanguageScreen.route) {
            LanguageSelectionScreen(
                settingsViewModel = settingsViewModel,
                onBackPressed = { navController.navigateUp() }
            )
        }
    }
}