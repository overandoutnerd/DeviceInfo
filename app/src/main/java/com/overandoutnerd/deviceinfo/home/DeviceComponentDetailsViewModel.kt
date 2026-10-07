package com.overandoutnerd.deviceinfo.home

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.models.TabItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceComponentsDetailsViewModel(context: Context): ViewModel() {

    private val _componentState = mutableStateOf(ComponentState())
    var componentState: State<ComponentState> = _componentState

    val repository = DeviceComponentsDetailsRepository(context)
    private var componentDetails by mutableStateOf<List<UserDeviceDetailsProperty?>>(emptyList())

    private val tabItems = listOf(
        TabItem(R.string.home_tabs_item_title_dashboard, "", icon = Icons.Rounded.Dashboard),
        TabItem(R.string.home_tabs_item_title_apps, "", icon = Icons.Rounded.Apps),
        TabItem(R.string.home_tabs_item_title_storage, "", icon = Icons.Rounded.Storage),
        TabItem(R.string.home_tabs_item_title_device, context.getString(R.string.home_device_details_flag), icon = Icons.Rounded.PhoneAndroid),
        TabItem(R.string.home_tabs_item_title_system, context.getString(R.string.home_system_details_flag), icon = Icons.Rounded.Memory),
        TabItem(R.string.home_tabs_item_title_processor, context.getString(R.string.home_processor_details_flag), icon = Icons.Rounded.Speed),
        TabItem(R.string.home_tabs_item_title_battery, context.getString(R.string.home_battery_details_flag), icon = Icons.Rounded.BatteryFull),
        TabItem(R.string.home_tabs_item_title_display, context.getString(R.string.home_display_details_flag), icon = Icons.Rounded.Tv),
        TabItem(R.string.home_tabs_item_title_camera, context.getString(R.string.home_camera_details_flag), icon = Icons.Rounded.PhotoCamera),
        TabItem(R.string.home_tabs_item_title_sensors, "sensors", Icons.Rounded.Sensors)
    )
    private val _tabItemsState = mutableStateOf(tabItems)
    val tabItemsState: State<List<TabItem>> = _tabItemsState

    private val _selectedTabItem = mutableIntStateOf(0)
    var selectedTabItem: State<Int> = _selectedTabItem

    fun updateSelectedTabItem(index: Int) {
        _selectedTabItem.intValue = index
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadComponentDetails(componentName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                componentDetails = repository.getDetails(componentName)
                _componentState.value = _componentState.value.copy(
                    loading = false,
                    componentDetails = componentDetails,
                    error = null
                )
            } catch (e: Exception) {
                _componentState.value = _componentState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchComponentDetails(componentName: String) {
        _componentState.value = ComponentState()
        loadComponentDetails(componentName)
    }

    data class ComponentState(
        var loading: Boolean = true,
        var componentDetails: List<UserDeviceDetailsProperty?> = emptyList(),
        var error: String? = null
    )

}