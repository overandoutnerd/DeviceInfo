package com.overandoutnerd.deviceinfo.home.dashboard

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class DashboardViewModel(context: Context): ViewModel() {

    private var appsCount = DashboardCountItemState(
        loading = true,
        item = DashboardCountItem(
            R.drawable.ic_android_outlined,
            R.string.dashboard_item_apps_title,
            0
        ),
        error = null
    )
    private var sensorCount = DashboardCountItemState(
        loading = true,
        item = DashboardCountItem(
            R.drawable.ic_sensor,
            R.string.dashboard_item_sensors_title,
            0
        ),
        error = null
    )
    private val storageDetails = DashboardProgressItemState(
        loading = true,
        item = DashboardProgressItem(
            R.drawable.ic_storage,
            R.string.dashboard_item_storage_title,
            R.string.dashboard_item_storage_subtitle,
        ),
        error = null
    )
    private val batteryDetailsState = DashboardProgressItemState(
        loading = true,
        item = DashboardProgressItem(
            R.drawable.ic_sensor, // TODO: Replace with battery icon
            R.string.home_tabs_item_title_battery,
            R.string.dashboard_item_battery_subtitle,
        ),
        error = null
    )

    private val _ramDetailsState = mutableStateOf(RamDetailsState())
    val ramDetailsState: State<RamDetailsState> = _ramDetailsState

    private val _processorDetailsState = mutableStateOf(ProcessorDetailsState())
    val processorDetailsState: State<ProcessorDetailsState> = _processorDetailsState

    private val _appsCountState = mutableStateOf(appsCount)
    val appsCountState: State<DashboardCountItemState> = _appsCountState

    private val _sensorsCountState = mutableStateOf(sensorCount)
    val sensorCountState: State<DashboardCountItemState> = _sensorsCountState

    private val _displayDetailsState = mutableStateOf(DisplayDetailsState())
    val displayDetailsState: State<DisplayDetailsState> = _displayDetailsState

    private val _storageDetailsState = mutableStateOf(storageDetails)
    val storageDetailsState: State<DashboardProgressItemState> = _storageDetailsState

    private val _batteryDetailsLiveData = MutableLiveData<DashboardProgressItemState>(batteryDetailsState)
    val batteryDetailsLiveData: MutableLiveData<DashboardProgressItemState> = _batteryDetailsLiveData

    val repository = DashboardRepository(context)

    init {
        observeRamUsage()
        observeProcessorDetails()
        getStorageDetails()
        getAppsCount()
        getSensorsCount()
        getDisplayDetails()
        observeBatteryDetails(context)
    }

    private fun observeBatteryDetails(context: Context) {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val percentage = level * 100 / scale.toFloat()
                    val voltage = it.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                    val temperature = it.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                    
                    val batteryDetails = BatteryDetails(
                        level = level,
                        voltage = "$voltage mV",
                        temperature = "${temperature / 10f} °C",
                        progress = percentage,
                        value = "$level%"
                    )

                    _batteryDetailsLiveData.postValue(
                        _batteryDetailsLiveData.value?.copy(
                            loading = false,
                            item = _batteryDetailsLiveData.value?.item?.copy(
                                batteryDetails = batteryDetails
                            )!!
                        )
                    )
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, intentFilter)
        }
    }

    private fun observeRamUsage(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                while (true) {
                    val ramDetails = repository.getRamDetails()
                    _ramDetailsState.value = RamDetailsState(
                        loading = false,
                        ramDetails = ramDetails
                    )
                    delay(1000.milliseconds)
                }
            } catch (e: Exception) {
                _ramDetailsState.value = RamDetailsState(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    private fun observeProcessorDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                while (true) {
                    val processorDetails = repository.getProcessorDetails()
                    _processorDetailsState.value = ProcessorDetailsState(
                        loading = false,
                        processorDetails = processorDetails
                    )
                    delay(1000)
                }
            } catch (e: Exception) {
                _processorDetailsState.value = ProcessorDetailsState(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    private fun getAppsCount() {
        var appsCount by mutableIntStateOf(0)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                appsCount = repository.getAppsCount()
                _appsCountState.value = _appsCountState.value.copy(
                    loading = false,
                    item = _appsCountState.value.item.copy(
                        value = appsCount
                    )
                )
            } catch (e: Exception) {
                _appsCountState.value = _appsCountState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    private fun getSensorsCount() {
        var sensorsCount by mutableIntStateOf(0)
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("Dashboard Repository", "getSensorsCount(): Coroutine scope launched")
            try {
                Log.e("Dashboard Repository", "getSensorsCount(): Calling getSensorsCount() of repository")
                sensorsCount = repository.getSensorsCount()
                _sensorsCountState.value = _sensorsCountState.value.copy(
                    loading = false,
                    item = _sensorsCountState.value.item.copy(
                        value = sensorsCount
                    )
                )
            } catch (e: Exception) {
                _sensorsCountState.value = _sensorsCountState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    private fun getDisplayDetails() {
        var displayDetails by mutableStateOf<DisplayDetails?>(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                displayDetails = repository.getDisplayDetails()
                _displayDetailsState.value = _displayDetailsState.value.copy(
                    loading = false,
                    displayDetails = displayDetails
                )
            } catch (e: Exception) {
                _displayDetailsState.value = _displayDetailsState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    private fun getStorageDetails() {
        var storageDetails by mutableStateOf<StorageDetails>(StorageDetails())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                storageDetails = repository.getStorageDetails()
                _storageDetailsState.value = _storageDetailsState.value.copy(
                    loading = false,
                    item = _storageDetailsState.value.item.copy(
                        storageDetails = storageDetails
                    ),
                )
            } catch (e: Exception) {
                _storageDetailsState.value = _storageDetailsState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    data class RamDetailsState(
        val loading: Boolean = true,
        val ramDetails: RamDetails = RamDetails(),
        val error: String? = null
    )

    data class ProcessorDetailsState(
        val loading: Boolean = true,
        val processorDetails: ProcessorDetails = ProcessorDetails("", emptyList()),
        val error: String? = null
    )

    data class DashboardCountItemState(
        val loading: Boolean = true,
        val item: DashboardCountItem = DashboardCountItem(),
        val error: String? = null
    )

    data class DisplayDetailsState(
        val loading: Boolean = true,
        val displayDetails: DisplayDetails? = null,
        val error: String? = null
    )

    data class DashboardProgressItemState(
        val loading: Boolean = true,
        val item: DashboardProgressItem = DashboardProgressItem(),
        val error: String? = null
    )

}