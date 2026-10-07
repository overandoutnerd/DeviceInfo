package com.overandoutnerd.deviceinfo.home.dashboard

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.StatFs
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.overandoutnerd.deviceinfo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.DecimalFormat

data class RamDetails(
    val total: String = "",
    val used: String = "",
    val available: String = "",
    val progress: Float = 0f
)

data class ProcessorDetails(
    val title: String,
    val cores: List<CPUCore>
)

data class CPUCore(
    val name: String,
    val frequency: String
)

data class DisplayDetails(
    val title: String,
    val resolution: String,
    val size: String,
    val refreshRate: String
)

data class StorageDetails(
    val total: String = "",
    val free: String = "",
    val progress: Float = 0f,
    val value: String = ""
)

data class BatteryDetails(
    val level: Int = 0,
    val voltage: String = "",
    val temperature: String = "",
    val progress: Float = 0f,
    val value: String = ""
)

data class DashboardProgressItem(
    @DrawableRes val icon: Int = 0,
    @StringRes val title: Int = 0,
    @StringRes val subTitle: Int = 0,
    val storageDetails: StorageDetails = StorageDetails(),
    val batteryDetails: BatteryDetails = BatteryDetails()
)

data class DashboardCountItem(
    @DrawableRes val icon: Int = 0,
    @StringRes val title: Int = 0,
    val value: Int = 0
)

class DashboardRepository(private val context: Context) {

    fun getRamDetails(): RamDetails {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem/(1024*1024)
        val usedRam = totalRam - memoryInfo.availMem/(1024*1024)
        val availableRam = memoryInfo.availMem/(1024*1024)
        val percentage = (usedRam.toDouble()/totalRam.toDouble()) * 100
        val progress = percentage.toFloat()
        return RamDetails(
            context.getString(R.string.dashboard_item_ram_total, totalRam.toString()),
            context.getString(R.string.dashboard_item_ram_used, usedRam.toString()),
            context.getString(R.string.dashboard_item_ram_free, availableRam.toString()),
            progress
        )
    }

    suspend fun getProcessorDetails(): ProcessorDetails {
        return withContext(Dispatchers.IO) {
            val cpuCores: MutableList<CPUCore> = mutableListOf()
            try {
                val totalCores = Runtime.getRuntime().availableProcessors()
                var currentIndex: Int = 0
                do {
                    val cpuCore = CPUCore("${context.getString(R.string.core_text)} ${currentIndex+1}", getCoreFrequency(currentIndex))
                    cpuCores.add(cpuCore)
                    currentIndex++
                } while (currentIndex < totalCores)
                ProcessorDetails(context.getString(R.string.dashboard_item_title_cpu_frequency), cpuCores)
            } catch (e: Exception) {
                Log.e("Dashboard Repository", "getProcessorDetails ${e.message}")
                ProcessorDetails("CPU Frequency", cpuCores)
            }
        }
    }

    suspend fun getAppsCount(): Int {
        return withContext(Dispatchers.IO) {
            var appsCount: Int = 0
            try {
                val packageManager: PackageManager = context.packageManager
                val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                appsCount = installedApps.size
                appsCount
            } catch(e: Exception){
                Log.e("Dashboard Repository", "getAppsCount ${e.message}")
                appsCount
            }
        }
    }

    suspend fun getSensorsCount(): Int {
        return withContext(Dispatchers.IO) {
            var sensorsCount: Int = 0
            try {
                val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
                sensorsCount = sensors.size
                sensorsCount
            } catch(e: Exception){
                Log.e("Dashboard Repository", "getSensorsCount ${e.message}")
                sensorsCount
            }
        }
    }

    suspend fun getDisplayDetails(): DisplayDetails? {
        return withContext(Dispatchers.IO) {
            var displayDetails: DisplayDetails? = null
            try {
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

                val display = windowManager.defaultDisplay
                val metrics = DisplayMetrics()
                display.getMetrics(metrics)

                val resolution = "${metrics.heightPixels} x ${metrics.widthPixels}"
                val size = getDisplayPhysicalSize()
                val refreshRate = display.refreshRate.toString()
                displayDetails = DisplayDetails(
                    context.getString(R.string.display_text),
                    resolution,
                    size,
                    "$refreshRate Hz"
                )
                displayDetails
            } catch (e: Exception) {
                Log.e("Dashboard Repository", "getDisplayDetails ${e.message}")
                displayDetails
            }
        }
    }

    suspend fun getStorageDetails(): StorageDetails {
        return withContext(Dispatchers.IO) {

            var storageDetails: StorageDetails = StorageDetails()
            try {
                val internalStats = StatFs("/data")
                val totalInternalStorageBytes = internalStats.blockCountLong * internalStats.blockSizeLong
                val usedInternalStorageBytes = totalInternalStorageBytes - (internalStats.availableBlocksLong * internalStats.blockSizeLong)

                val freeValue = totalInternalStorageBytes-usedInternalStorageBytes
                val total: Double = totalInternalStorageBytes.toDouble()/1024/1024/1024
                val free: Double = freeValue.toDouble()/1024/1024/1024
                val formattedTotal = DecimalFormat("#.##").format(total)
                val formattedFree = DecimalFormat("#.##").format(free)
                val percentage: Double = (usedInternalStorageBytes.toDouble()/totalInternalStorageBytes.toDouble()) * 100
                val formattedPercentage =  DecimalFormat("#").format(percentage)

                val icon = R.drawable.ic_storage
                val title = context.getString(R.string.dashboard_item_storage_title)
                val subtitle = context.getString(
                    R.string.dashboard_item_storage_subtitle,
                    formattedFree,
                    formattedTotal
                )
                val progress: Float = percentage.toFloat()
                val value = "${formattedPercentage}%"
                storageDetails = StorageDetails(
                    formattedTotal,
                    formattedFree,
                    progress,
                    value
                )
                storageDetails
            } catch (e: Exception) {
                Log.e("Dashboard Repository", "getDisplayDetails ${e.message}")
                storageDetails
            }
        }
    }

    private fun getCoreFrequency(core: Int): String {
        val cpuFreqFile = "/sys/devices/system/cpu/cpu$core/cpufreq/scaling_cur_freq"
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