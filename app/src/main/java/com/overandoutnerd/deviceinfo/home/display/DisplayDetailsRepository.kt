package com.overandoutnerd.deviceinfo.home.display

import android.content.Context
import android.content.res.Configuration
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.home.UserDeviceDetailsProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DisplayDetailsRepository(private val context: Context) {

    suspend fun getDetails(): List<UserDeviceDetailsProperty> {
        return withContext(Dispatchers.IO) {
            val details: MutableList<UserDeviceDetailsProperty> = mutableListOf()
            try {

                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

                val display = windowManager.defaultDisplay
                val metrics = DisplayMetrics()
                display.getMetrics(metrics)

                val resolutionValue = "${metrics.widthPixels} x ${metrics.heightPixels} ${context.getString(R.string.pixels_default_title)}"
                val resolution = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_resolution), resolutionValue)
                details.add(resolution)

                val densityValue = "${metrics.densityDpi} dpi"
                val density = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_density), densityValue)
                details.add(density)

                val fontScale = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_font_scale), metrics.scaledDensity.toString())
                details.add(fontScale)

                val physicalSizeValue = getDisplayPhysicalSize()
                val physicalSize = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_physical_size), physicalSizeValue.toString())
                details.add(physicalSize)

                val refreshState = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_refresh_rate), display.refreshRate.toString())
                details.add(refreshState)

                val brightnessLevelValue = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
                val brightnessLevel = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_brightness_level), "$brightnessLevelValue (${(brightnessLevelValue/255)*100}%)")
                details.add(brightnessLevel)

                val brightnessModeValue = when(Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE)) {
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC -> context.getString(R.string.brightness_mode_item_title_automatic)
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL -> context.getString(R.string.brightness_mode_item_title_manual)
                    else -> context.getString(R.string.brightness_mode_item_title_unknown)
                }
                val brightnessMode = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_brightness_mode), brightnessModeValue)
                details.add(brightnessMode)

                val hdrValue = if (display.isHdr) context.getString(R.string.hdr_item_title_supported) else context.getString(R.string.hdr_item_title_not_supported)
                val hdr = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_hdr), hdrValue)
                details.add(hdr)

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
                details.add(hdrCapabilities)

                val screenTimeoutValue = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
                val screenTimeout = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_screen_timeout), "${screenTimeoutValue/1000} Seconds")
                details.add(screenTimeout)

                val orientationValue = when (context.resources.configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> context.getString(R.string.orientation_item_title_portrait)
                    Configuration.ORIENTATION_LANDSCAPE -> context.getString(R.string.orientation_item_title_landscape)
                    Configuration.ORIENTATION_UNDEFINED -> context.getString(R.string.orientation_item_title_undefined)
                    else -> context.getString(R.string.orientation_item_title_unknown)
                }
                val orientation = UserDeviceDetailsProperty(context.getString(R.string.display_details_item_title_orientation), orientationValue)
                details.add(orientation)

                details
            } catch (e: Exception) {
                Log.e(
                    "DisplayDetailsRepository",
                    "Error while retrieving details: ${e.message}"
                )
                details
            }
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