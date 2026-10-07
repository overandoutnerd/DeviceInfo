package com.overandoutnerd.deviceinfo.home.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.home.UserDeviceDetailsProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BatteryDetailsRepository(
    private val context: Context,
    private val viewModel: BatteryDetailsViewModel
): BroadcastReceiver() {

    suspend fun getDetails(): List<UserDeviceDetailsProperty> {
        return withContext(Dispatchers.IO) {
            val details: MutableList<UserDeviceDetailsProperty> = mutableListOf()
            try {
                val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                val batteryStatusIntent: Intent? = context.registerReceiver(null, intentFilter)
                val healthValue = when(batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
                    BatteryManager.BATTERY_HEALTH_GOOD -> context.getString(R.string.battery_health_item_title_good)
                    BatteryManager.BATTERY_HEALTH_UNKNOWN -> context.getString(R.string.battery_health_item_title_unknown)
                    BatteryManager.BATTERY_HEALTH_OVERHEAT -> context.getString(R.string.battery_health_item_title_overheat)
                    BatteryManager.BATTERY_HEALTH_DEAD -> context.getString(R.string.battery_health_item_title_dead)
                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> context.getString(R.string.battery_health_item_title_over_voltage)
                    BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> context.getString(R.string.battery_health_item_title_unspecified_failure)
                    else -> context.getString(R.string.battery_health_item_title_unknown)
                }
                val health = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_health), healthValue)
                details.add(health)
                val levelValue = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
                val level = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_level), levelValue.toString())
                details.add(level)
                val statusValue = when(batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)) {
                    BatteryManager.BATTERY_STATUS_CHARGING -> context.getString(R.string.battery_status_item_title_charging)
                    BatteryManager.BATTERY_STATUS_DISCHARGING -> context.getString(R.string.battery_status_item_title_discharging)
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> context.getString(R.string.battery_status_item_title_not_charging)
                    BatteryManager.BATTERY_STATUS_FULL -> context.getString(R.string.battery_status_item_title_full_charged)
                    else -> context.getString(R.string.battery_status_item_title_unknown)
                }
                val status = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_status), statusValue)
                details.add(status)
                val powerSourceValue = when(batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
                    BatteryManager.BATTERY_PLUGGED_AC -> context.getString(R.string.battery_power_source_item_title_ac)
                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> context.getString(R.string.battery_power_source_item_title_wireless)
                    BatteryManager.BATTERY_PLUGGED_USB -> context.getString(R.string.battery_power_source_item_title_usb)
                    BatteryManager.BATTERY_PLUGGED_DOCK -> context.getString(R.string.battery_power_source_item_title_dock)
                    else -> context.getString(R.string.battery_power_source_item_title_battery)
                }
                val powerSource = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_power_source), powerSourceValue)
                details.add(powerSource)
                val technologyValue = batteryStatusIntent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: ""
                val technology = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_technology), technologyValue)
                details.add(technology)
                val temperatureValue = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
                val temperature = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_temperature), "${temperatureValue / 10} ℃")
                details.add(temperature)
                val currentValue = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                val current = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_current), "$currentValue mA")
                details.add(current)
                val voltageValue = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
                val voltage = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_voltage), voltageValue.toString())
                details.add(voltage)
                val capacityValue = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 0
                val capacity = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_capacity), capacityValue.toString())
                details.add(capacity)

                details
            } catch (e: Exception) {
                Log.e(
                    "BatteryDetailsRepository",
                    "Error while retrieving details: ${e.message}"
                )
                details
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("BatteryDetailsRepository", "onReceive() triggered")
        val details: MutableList<UserDeviceDetailsProperty> = mutableListOf()
        try {
            val batteryManager = context?.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            intent?.let { batteryStatusIntent ->
                val healthValue = when(batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
                    BatteryManager.BATTERY_HEALTH_GOOD -> context.getString(R.string.battery_health_item_title_good)
                    BatteryManager.BATTERY_HEALTH_UNKNOWN -> context.getString(R.string.battery_health_item_title_unknown)
                    BatteryManager.BATTERY_HEALTH_OVERHEAT -> context.getString(R.string.battery_health_item_title_overheat)
                    BatteryManager.BATTERY_HEALTH_DEAD -> context.getString(R.string.battery_health_item_title_dead)
                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> context.getString(R.string.battery_health_item_title_over_voltage)
                    BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> context.getString(R.string.battery_health_item_title_unspecified_failure)
                    else -> context.getString(R.string.battery_health_item_title_unknown)
                }
                Log.e("BatteryDetailsRepository", "Inside onReceive(). Battery Health: $healthValue")
                val health = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_health), healthValue)
                details.add(health)
                val levelValue = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
                val level = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_level), levelValue.toString())
                details.add(level)
                val statusValue = when(batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)) {
                    BatteryManager.BATTERY_STATUS_CHARGING -> context.getString(R.string.battery_status_item_title_charging)
                    BatteryManager.BATTERY_STATUS_DISCHARGING -> context.getString(R.string.battery_status_item_title_discharging)
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> context.getString(R.string.battery_status_item_title_not_charging)
                    BatteryManager.BATTERY_STATUS_FULL -> context.getString(R.string.battery_status_item_title_full_charged)
                    else -> context.getString(R.string.battery_status_item_title_unknown)
                }
                val status = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_status), statusValue)
                details.add(status)
                val powerSourceValue = when(batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
                    BatteryManager.BATTERY_PLUGGED_AC -> context.getString(R.string.battery_power_source_item_title_ac)
                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> context.getString(R.string.battery_power_source_item_title_wireless)
                    BatteryManager.BATTERY_PLUGGED_USB -> context.getString(R.string.battery_power_source_item_title_usb)
                    BatteryManager.BATTERY_PLUGGED_DOCK -> context.getString(R.string.battery_power_source_item_title_dock)
                    else -> context.getString(R.string.battery_power_source_item_title_battery)
                }
                val powerSource = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_power_source), powerSourceValue)
                details.add(powerSource)
                val technologyValue = batteryStatusIntent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: ""
                val technology = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_technology), technologyValue)
                details.add(technology)
                val temperatureValue = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
                val temperature = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_temperature), "${temperatureValue / 10} ℃")
                details.add(temperature)
                val currentValue = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                val current = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_current), "$currentValue mA")
                details.add(current)
                val voltageValue = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
                val voltage = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_voltage), voltageValue.toString())
                details.add(voltage)
                val capacityValue = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 0
                val capacity = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_capacity), capacityValue.toString())
                details.add(capacity)
                viewModel.loadDetails(details)
            }
        } catch (e: Exception) {
            Log.e(
                "BatteryDetailsRepository",
                "Error while retrieving details: ${e.message}"
            )
        }
    }

}