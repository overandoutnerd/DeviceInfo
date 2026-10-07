package com.overandoutnerd.deviceinfo.home.system

import android.content.Context
import android.os.Build
import android.util.Log
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.home.UserDeviceDetailsProperty
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone

class SystemDetailsRepository(private val context: Context) {

    suspend fun getDetails(): List<UserDeviceDetailsProperty> {
        return withContext(Dispatchers.IO) {
            val details: MutableList<UserDeviceDetailsProperty> = mutableListOf()
            try {

                val androidVersion = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_android_version), "${context.getString(
                    R.string.android_system_title)} " + Build.VERSION.RELEASE)
                details.add(androidVersion)
                val codeName = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_code_name), Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name)
                details.add(codeName)
                val apiLevel = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_api_level), Build.VERSION.SDK_INT.toString())
                details.add(apiLevel)
                val securityPatch = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_security_patch), Build.VERSION.SECURITY_PATCH)
                details.add(securityPatch)
                val bootloader = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_bootloader), Build.BOOTLOADER)
                details.add(bootloader)
                val buildNumber = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_build_number), Build.DISPLAY)
                details.add(buildNumber)
                val baseband = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_baseband), Build.getRadioVersion())
                details.add(baseband)
                val javaVm = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_java_vm), System.getProperty("java.vm.version"))
                details.add(javaVm)
                val kernel = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_kernel), System.getProperty("os.version"))
                details.add(kernel)
                val language = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_language), Locale.getDefault().toString())
                details.add(language)
                val timezone = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_timezone), TimeZone.getDefault().id)
                details.add(timezone)
                val googlePlayServicesInfo = context.packageManager.getPackageInfo(
                    GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0)
                val googlePlayServices = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_google_play_services), googlePlayServicesInfo.versionName ?: "")
                details.add(googlePlayServices)

                details
            } catch (e: Exception) {
                Log.e(
                    "SystemDetailsRepository",
                    "Error while retrieving details: ${e.message}"
                )
                details
            }
        }
    }

}