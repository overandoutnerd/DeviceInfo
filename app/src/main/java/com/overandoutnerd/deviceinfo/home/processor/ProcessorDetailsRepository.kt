package com.overandoutnerd.deviceinfo.home.processor

import android.content.Context
import android.os.Build
import android.util.Log
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.home.UserDeviceDetailsProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

class ProcessorDetailsRepository(private val context: Context) {

    suspend fun getDetails(): List<UserDeviceDetailsProperty> {
        return withContext(Dispatchers.IO) {
            val processorDetails: MutableList<UserDeviceDetailsProperty> = mutableListOf()

            try {

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
                Log.e(
                    "ProcessorDetailsRepository",
                    "Error while retrieving details: ${e.message}"
                )
                processorDetails
            }
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
}