package com.overandoutnerd.deviceinfo.home.device

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.home.UserDeviceDetailsProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeviceDetailsRepository(private val context: Context) {

    suspend fun getDetails(): List<UserDeviceDetailsProperty> {
        return withContext(Dispatchers.IO) {
            val details: MutableList<UserDeviceDetailsProperty> = mutableListOf()
            try {

                val deviceNameValue = Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME)
                val deviceName = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_name), deviceNameValue)
                details.add(deviceName)
                val deviceCodeName = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_code), Build.DEVICE)
                details.add(deviceCodeName)
                val model = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_model), Build.MODEL )
                details.add(model)
                val manufacturer = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_manufacturer), Build.MANUFACTURER)
                details.add(manufacturer)
                val board = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_board), Build.BOARD)
                details.add(board)
                val hardware = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_hardware), Build.HARDWARE)
                details.add(hardware)
                val brand = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_brand), Build.BRAND)
                details.add(brand)
                val androidDeviceId = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_id), Build.ID)
                details.add(androidDeviceId)
                val buildFingerprint = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_fingerprint), Build.FINGERPRINT)
                details.add(buildFingerprint)

                details
            } catch (e: Exception) {
                Log.e(
                    "DeviceDetailsRepository",
                    "Error while retrieving details: ${e.message}"
                )
                details
            }
        }
    }

}