package com.overandoutnerd.deviceinfo.home

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.overandoutnerd.deviceinfo.R
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale
import java.util.TimeZone

class DeviceComponentsDetailsRepository(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDetails(componentName: String): List<UserDeviceDetailsProperty?> {
        return withContext(Dispatchers.IO) {
            var componentItemsList: List<UserDeviceDetailsProperty?> = emptyList()
            try {
                componentItemsList = when(componentName) {
                    context.getString(R.string.home_device_details_flag) -> getDeviceDetails()
                    context.getString(R.string.home_system_details_flag) -> getSystemDetails()
                    context.getString(R.string.home_processor_details_flag) -> getProcessorDetails()
                    context.getString(R.string.home_battery_details_flag) -> getBatteryDetails()
                    context.getString(R.string.home_display_details_flag) -> getDisplayDetails()
                    context.getString(R.string.home_camera_details_flag) -> getCameraDetails()
                    else -> getDeviceDetails()
                }
                componentItemsList
            } catch (e: Exception){
                Log.e("DeviceComponentsDetailsRepository", "Method: getDetails(), Error: ${e.message}")
                componentItemsList
            }
        }
    }

    private fun getDeviceDetails(): List<UserDeviceDetailsProperty?> {
        val deviceDetails: MutableList<UserDeviceDetailsProperty?> = mutableListOf()
        return try {
            val deviceNameValue = Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME)
            val deviceName = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_name), deviceNameValue)
            deviceDetails.add(deviceName)
            val deviceCodeName = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_code), Build.DEVICE)
            deviceDetails.add(deviceCodeName)
            val model = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_model), Build.MODEL )
            deviceDetails.add(model)
            val manufacturer = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_manufacturer), Build.MANUFACTURER)
            deviceDetails.add(manufacturer)
            val board = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_board), Build.BOARD)
            deviceDetails.add(board)
            val hardware = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_hardware), Build.HARDWARE)
            deviceDetails.add(hardware)
            val brand = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_brand), Build.BRAND)
            deviceDetails.add(brand)
            val androidDeviceId = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_id), Build.ID)
            deviceDetails.add(androidDeviceId)
            val buildFingerprint = UserDeviceDetailsProperty(context.getString(R.string.device_details_item_title_device_fingerprint), Build.FINGERPRINT)
            deviceDetails.add(buildFingerprint)

            deviceDetails
        } catch (e: Exception) {
            Log.e("DeviceComponentsDetailsRepository", "Method: getDeviceDetails(), Error: ${e.message}")
            deviceDetails
        }
    }

    private fun getSystemDetails(): List<UserDeviceDetailsProperty?> {
        val systemDetails: MutableList<UserDeviceDetailsProperty?> = mutableListOf()
        return try {
            val androidVersion = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_android_version), "${context.getString(R.string.android_system_title)} " + Build.VERSION.RELEASE)
            systemDetails.add(androidVersion)
            val codeName = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_code_name), Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name)
            systemDetails.add(codeName)
            val apiLevel = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_api_level), Build.VERSION.SDK_INT.toString())
            systemDetails.add(apiLevel)
            val securityPatch = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_security_patch), Build.VERSION.SECURITY_PATCH)
            systemDetails.add(securityPatch)
            val bootloader = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_bootloader), Build.BOOTLOADER)
            systemDetails.add(bootloader)
            val buildNumber = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_build_number), Build.DISPLAY)
            systemDetails.add(buildNumber)
            val baseband = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_baseband), Build.getRadioVersion())
            systemDetails.add(baseband)
            val javaVm = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_java_vm), System.getProperty("java.vm.version"))
            systemDetails.add(javaVm)
            val kernel = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_kernel), System.getProperty("os.version"))
            systemDetails.add(kernel)
            val language = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_language), Locale.getDefault().toString())
            systemDetails.add(language)
            val timezone = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_timezone), TimeZone.getDefault().id)
            systemDetails.add(timezone)
            val googlePlayServicesInfo = context.packageManager.getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0)
            val googlePlayServices = UserDeviceDetailsProperty(context.getString(R.string.system_details_item_title_google_play_services), googlePlayServicesInfo.versionName ?: "")
            systemDetails.add(googlePlayServices)

            systemDetails
        } catch (e: Exception) {
            Log.e("DeviceComponentsDetailsRepository", "Method: getSystemDetails(), Error: ${e.message}")
            systemDetails
        }
    }

    private fun getProcessorDetails(): List<UserDeviceDetailsProperty?> {
        val processorDetails: MutableList<UserDeviceDetailsProperty?> = mutableListOf()
        return try {
            val processorName = UserDeviceDetailsProperty(context.getString(R.string.processor_details_item_title_processor), Build.HARDWARE)
            processorDetails.add(processorName)
            val cpuArchitecture = UserDeviceDetailsProperty(context.getString(R.string.processor_details_item_title_cpu_architecture), Build.SUPPORTED_ABIS[0])
            processorDetails.add(cpuArchitecture)
            val supportedABIs = UserDeviceDetailsProperty(context.getString(R.string.processor_details_item_title_supported_abis), Build.SUPPORTED_ABIS.joinToString())
            processorDetails.add(supportedABIs)
            val cpuTypeValue = if(Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()) "64 Bit" else  "32 Bit"
            val cpuType = UserDeviceDetailsProperty(context.getString(R.string.processor_details_item_title_cpu_type), cpuTypeValue)
            processorDetails.add(cpuType)
            val cpuGovernor = UserDeviceDetailsProperty(context.getString(R.string.processor_details_item_title_cpu_governor), getCpuGovernor())
            processorDetails.add(cpuGovernor)
            val totalCores = UserDeviceDetailsProperty(context.getString(R.string.processor_details_item_title_total_cores), Runtime.getRuntime().availableProcessors().toString())
            processorDetails.add(totalCores)
            val cpuFrequency = UserDeviceDetailsProperty(context.getString(R.string.processor_details_item_title_cpu_frequency), getCpuFrequency())
            processorDetails.add(cpuFrequency)
            val gpuRenderer = UserDeviceDetailsProperty(context.getString(R.string.processor_details_item_title_gpu_renderer), readCpuInfoProperty("ro.hardware.gpu.renderer"))
            processorDetails.add(gpuRenderer)
            val gpuVendor = UserDeviceDetailsProperty(context.getString(R.string.processor_details_item_title_gpu_vendor), readCpuInfoProperty("ro.hardware.gpu.vendor"))
            processorDetails.add(gpuVendor)
            val gpuVersion = UserDeviceDetailsProperty(context.getString(R.string.processor_details_item_title_gpu_version), readCpuInfoProperty("ro.hardware.gpu.version"))
            processorDetails.add(gpuVersion)
            processorDetails
        } catch (e: Exception) {
            Log.e("DeviceComponentsDetailsRepository", "Method: getProcessorDetails(), Error: ${e.message}")
            processorDetails
        }
    }

    private fun getBatteryDetails(): List<UserDeviceDetailsProperty?> {
        val batteryDetails: MutableList<UserDeviceDetailsProperty?> = mutableListOf()
        return try {
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
            batteryDetails.add(health)
            val levelValue = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
            val level = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_level), levelValue.toString())
            batteryDetails.add(level)
            val statusValue = when(batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)) {
                BatteryManager.BATTERY_STATUS_CHARGING -> context.getString(R.string.battery_status_item_title_charging)
                BatteryManager.BATTERY_STATUS_DISCHARGING -> context.getString(R.string.battery_status_item_title_discharging)
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> context.getString(R.string.battery_status_item_title_not_charging)
                BatteryManager.BATTERY_STATUS_FULL -> context.getString(R.string.battery_status_item_title_full_charged)
                else -> context.getString(R.string.battery_status_item_title_unknown)
            }
            val status = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_status), statusValue)
            batteryDetails.add(status)
            val powerSourceValue = when(batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
                BatteryManager.BATTERY_PLUGGED_AC -> context.getString(R.string.battery_power_source_item_title_ac)
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> context.getString(R.string.battery_power_source_item_title_wireless)
                BatteryManager.BATTERY_PLUGGED_USB -> context.getString(R.string.battery_power_source_item_title_usb)
                BatteryManager.BATTERY_PLUGGED_DOCK -> context.getString(R.string.battery_power_source_item_title_dock)
                else -> context.getString(R.string.battery_power_source_item_title_battery)
            }
            val powerSource = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_power_source), powerSourceValue)
            batteryDetails.add(powerSource)
            val technologyValue = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_TECHNOLOGY, 0) ?: ""
            val technology = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_technology), technologyValue.toString())
            batteryDetails.add(technology)
            val temperatureValue = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
            val temperature = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_temperature), temperatureValue.toString())
            batteryDetails.add(temperature)
            val voltageValue = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
            val voltage = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_voltage), voltageValue.toString())
            batteryDetails.add(voltage)
            val capacityValue = batteryStatusIntent?.getIntExtra(BatteryManager.BATTERY_PROPERTY_CAPACITY.toString(), 0) ?: 0
            val capacity = UserDeviceDetailsProperty(context.getString(R.string.battery_details_item_title_capacity), capacityValue.toString())
            batteryDetails.add(capacity)
            batteryDetails
        } catch (e: Exception) {
            Log.e("DeviceComponentsDetailsRepository", "Method: getBatteryDetails(), Error: ${e.message}")
            batteryDetails
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDisplayDetails(): List<UserDeviceDetailsProperty?> {
        val displayDetails: MutableList<UserDeviceDetailsProperty?> = mutableListOf()
        return try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            val display = windowManager.defaultDisplay
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)

            val resolutionValue = "${metrics.widthPixels} x ${metrics.heightPixels} ${context.getString(R.string.pixels_default_title)}"
            val resolution = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_resolution), resolutionValue)
            displayDetails.add(resolution)

            val densityValue = "${metrics.densityDpi} dpi"
            val density = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_density), densityValue)
            displayDetails.add(density)

            val fontScale = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_font_scale), metrics.scaledDensity.toString())
            displayDetails.add(fontScale)

            val physicalSizeValue = getDisplayPhysicalSize()
            val physicalSize = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_physical_size), physicalSizeValue.toString())
            displayDetails.add(physicalSize)

            val refreshState = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_refresh_rate), display.refreshRate.toString())
            displayDetails.add(refreshState)

            val brightnessLevelValue = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            val brightnessLevel = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_brightness_level), "$brightnessLevelValue (${(brightnessLevelValue/255)*100}%)")
            displayDetails.add(brightnessLevel)

            val brightnessModeValue = when(Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE)) {
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC -> context.getString(R.string.brightness_mode_item_title_automatic)
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL -> context.getString(R.string.brightness_mode_item_title_manual)
                else -> context.getString(R.string.brightness_mode_item_title_unknown)
            }
            val brightnessMode = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_brightness_mode), brightnessModeValue)
            displayDetails.add(brightnessMode)

            val hdrValue = if (display.isHdr) context.getString(R.string.hdr_item_title_supported) else context.getString(R.string.hdr_item_title_not_supported)
            val hdr = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_hdr), hdrValue)
            displayDetails.add(hdr)

            val hdrCapabilitiesValue = display.hdrCapabilities
            val hdrMaxLuminance = hdrCapabilitiesValue?.desiredMaxLuminance ?: 0.0f
            val hdrMaxAverageLuminance = hdrCapabilitiesValue?.desiredMaxAverageLuminance ?: 0.0f
            val hdrMinLuminance = hdrCapabilitiesValue?.desiredMinLuminance ?: 0.0f
            val hdrCapabilitiesString = """
                    |${context.getString(R.string.hdr_capabilities_item_title_max_luminance)}: $hdrMaxLuminance
                    |${context.getString(R.string.hdr_capabilities_item_title_max_average_luminance)}: $hdrMaxAverageLuminance
                    |${context.getString(R.string.hdr_capabilities_item_title_min_luminance)}: $hdrMinLuminance
                """.trimMargin()
            val hdrCapabilities = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_hdr_capabilities), hdrCapabilitiesString)
            displayDetails.add(hdrCapabilities)

            val screenTimeoutValue = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
            val screenTimeout = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_screen_timeout), "${screenTimeoutValue/1000} Seconds")
            displayDetails.add(screenTimeout)

            val orientationValue = when (context.resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> context.getString(R.string.orientation_item_title_portrait)
                Configuration.ORIENTATION_LANDSCAPE -> context.getString(R.string.orientation_item_title_landscape)
                Configuration.ORIENTATION_UNDEFINED -> context.getString(R.string.orientation_item_title_undefined)
                else -> context.getString(R.string.orientation_item_title_unknown)
            }
            val orientation = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_orientation), orientationValue)
            displayDetails.add(orientation)

            displayDetails
        } catch (e: Exception) {
            Log.e("DeviceComponentsDetailsRepository", "Method: getDisplayDetails(), Error: ${e.message}")
            displayDetails
        }
    }

    private fun getCameraDetails(): List<UserDeviceDetailsProperty?> {
        val cameraDetails: MutableList<UserDeviceDetailsProperty?> = mutableListOf()
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraIds = cameraManager.cameraIdList
            var frontCameraId: String? = null
            for (id in cameraIds) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontCameraId = id
                    break
                }
            }
            if (frontCameraId != null) {
                val characteristics = cameraManager.getCameraCharacteristics(frontCameraId)
                val pixelArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)
                val megaPixelsValue = if (pixelArraySize != null) {
                    (pixelArraySize.width * pixelArraySize.height) / 1_000_000.0
                } else { 0.0 }
                val megaPixels = UserDeviceDetailsProperty("Mega Pixels", megaPixelsValue.toString())
                cameraDetails.add(megaPixels)
                val aberrationModesValue = characteristics.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES)
                aberrationModesValue?.let {
                    val aberrationModesNames = it.map { mode ->
                        when (mode) {
                            CameraCharacteristics.COLOR_CORRECTION_ABERRATION_MODE_OFF -> "OFF"
                            CameraCharacteristics.COLOR_CORRECTION_ABERRATION_MODE_FAST -> "Fast"
                            CameraCharacteristics.COLOR_CORRECTION_ABERRATION_MODE_HIGH_QUALITY -> "High Quality"
                            else -> "Unknown"
                        }
                    }
                    val aberrationModes = UserDeviceDetailsProperty("Aberration Modes", aberrationModesNames.joinToString())
                    cameraDetails.add(aberrationModes)
                }
                val antibandingModesValue = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES)
                antibandingModesValue?.let {
                    val antibandingModesNames = it.map { mode ->
                        when (mode) {
                            CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_OFF -> "Off"
                            CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_AUTO -> "Auto"
                            CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_50HZ -> "50Hz"
                            CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_60HZ -> "60Hz"
                            else -> "Unknown"
                        }
                    }
                    val antibandingModes = UserDeviceDetailsProperty("Antibanding Modes", antibandingModesNames.joinToString())
                    cameraDetails.add(antibandingModes)
                }
                val autoExposureModesValue = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES)
                autoExposureModesValue?.let {
                    val autoExposureModesNames = it.map { mode ->
                        when (mode) {
                            CameraCharacteristics.CONTROL_AE_MODE_ON -> "On"
                            CameraCharacteristics.CONTROL_AE_MODE_OFF -> "Off"
                            CameraCharacteristics.CONTROL_AE_MODE_ON_AUTO_FLASH -> "Auto Flash"
                            CameraCharacteristics.CONTROL_AE_MODE_ON_ALWAYS_FLASH -> "Always Flash"
                            CameraCharacteristics.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE -> "Auto Flash Red Eye"
                            CameraCharacteristics.CONTROL_AE_MODE_ON_EXTERNAL_FLASH -> "External Flash"
                            else -> "Unknown"
                        }
                    }
                    val autoExposureModes = UserDeviceDetailsProperty("Auto Exposure Modes", autoExposureModesNames.joinToString())
                    cameraDetails.add(autoExposureModes)
                }
                val autoFocusModesValue = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)
                autoFocusModesValue?.let {
                    val autoFocusModesNames = it.map { mode ->
                        when (mode) {
                            CameraCharacteristics.CONTROL_AF_MODE_OFF -> "Off"
                            CameraCharacteristics.CONTROL_AF_MODE_AUTO -> "Auto"
                            CameraCharacteristics.CONTROL_AF_MODE_EDOF -> "Digital"
                            CameraCharacteristics.CONTROL_AF_MODE_MACRO -> "Macro"
                            CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_PICTURE -> "Continuous Picture"
                            CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_VIDEO -> "Continuous Video"
                            else -> "Unknown"
                        }
                    }
                    val autoFocusModes = UserDeviceDetailsProperty("Auto Focus Modes", autoFocusModesNames.joinToString())
                    cameraDetails.add(autoFocusModes)
                }
            }
            cameraDetails
        } catch (e: Exception) {
            Log.e("DeviceComponentsDetailsRepository", "Method: getCameraDetails(), Error: ${e.message}")
            cameraDetails
        }
    }

    private fun getCpuGovernor(): String {
        return try {
            val process = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor")
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            val cpuGovernor = bufferedReader.readLine()
            bufferedReader.close()
            cpuGovernor ?: "Unknown"
        } catch (e: Exception){
            Log.e("getCpuGovernor", "${e.message}")
            "Unidentified"
        }
    }

    private fun readCpuInfoProperty(property: String): String {
        return try {
            val initialResult = ProcessBuilder("getprop", property)
            val result = ProcessBuilder("getprop", property)
                .redirectErrorStream(true)
                .start()
                .inputStream
                .bufferedReader()
                .use { it.readText().trim() }
            Log.e("readCpuInfoProperty", result)
            result
        } catch (e: Exception){
            "N/A"
        }
    }

    private fun getCpuFrequency(): String {
        val cpuFreqFile = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
        return try {
            val frequency = File(cpuFreqFile).readLines()[0].toInt() / 1000 // converting to MHz
            "$frequency MHz"
        } catch (e: IOException) {
            Log.e("getCpuFrequency", "${e.message}")
            "Error reading CPU frequency"
        } catch (e: NumberFormatException) {
            Log.e("getCpuFrequency", "${e.message}")
            "Invalid CPU frequency format"
        }
    }

    private fun getDisplayPhysicalSize(): String {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val widthPixels = displayMetrics.widthPixels
        val heightPixels = displayMetrics.heightPixels

        val xdpi = displayMetrics.xdpi
        val ydpi = displayMetrics.ydpi

        val screenWidthInches = widthPixels / xdpi
        val screenHeightInches = heightPixels / ydpi

        return "%.1f ${context.getString(R.string.inches_default_title)}".format(
            kotlin.math.sqrt((screenWidthInches * screenWidthInches) + (screenHeightInches * screenHeightInches))
        )
    }

}