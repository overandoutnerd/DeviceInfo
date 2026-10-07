package com.overandoutnerd.deviceinfo.home.storage

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.util.Log
import com.overandoutnerd.deviceinfo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.RandomAccessFile
import java.lang.Exception
import java.text.DecimalFormat
import java.util.regex.Pattern

data class StorageDetails(
    val title: String,
    val subtitle: String,
    val value: String,
    val progress: Float
)

data class ProcessDetails(
    val processId: Int,
    val processName: String,
    val appName: String,
    val appIcon: Drawable,
    val memoryUsage: Int
)

class StorageDetailsRepository(private val context: Context) {

    fun getMemoryInfoListFlow(updateIntervalMs: Long = 1000L): Flow<List<MemoryInfo>> = flow {
        while (currentCoroutineContext().isActive) {
            emit(getMemoryInfoList())
            delay(updateIntervalMs)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getMemoryInfoList(): List<MemoryInfo> {
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<MemoryInfo>()
            try {
                // 1. RAM Memory
                list.add(getRamMemoryInfo())

                // 2. Internal Storage
                list.add(getInternalStorageInfo())

                // 3. SD Card / External Storage (if available)
                getExternalStorageInfo()?.let { list.add(it) }

            } catch (e: Exception) {
                Log.e("StorageDetailsRepository", "Error loading MemoryInfo list: ${e.message}")
            }
            list
        }
    }

    private fun getRamMemoryInfo(): MemoryInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalRam = memoryInfo.totalMem
        val availRam = memoryInfo.availMem
        val usedRam = totalRam - availRam

        val procMemMap = parseProcMemInfo()

        val users = if (procMemMap.isNotEmpty()) {
            val memFree = procMemMap["MemFree"] ?: availRam
            val buffers = procMemMap["Buffers"] ?: 0L
            val cached = procMemMap["Cached"] ?: 0L
            val sReclaimable = procMemMap["SReclaimable"] ?: 0L
            val active = procMemMap["Active"] ?: 0L
            val inactive = procMemMap["Inactive"] ?: 0L

            val cacheUsage = (cached + buffers + sReclaimable).coerceAtMost(usedRam)
            val freeUsage = memFree.coerceAtMost(totalRam)

            val appsUsage = ((active + inactive) - cacheUsage).coerceAtLeast(0L).coerceAtMost(usedRam)
            val systemAndReservedUsage = (totalRam - (appsUsage + cacheUsage + freeUsage)).coerceAtLeast(0L)

            listOf(
                MemoryUser(context.getString(R.string.memory_user_apps), appsUsage),
                MemoryUser(context.getString(R.string.memory_user_cache), cacheUsage),
                MemoryUser(context.getString(R.string.memory_user_system_and_reserved), systemAndReservedUsage),
                MemoryUser(context.getString(R.string.memory_user_free), freeUsage)
            )
        } else {
            val cacheUsage = (usedRam * 0.25).toLong()
            val appsUsage = (usedRam * 0.50).toLong()
            val systemAndReservedUsage = (usedRam - (appsUsage + cacheUsage)).coerceAtLeast(0L)
            val freeUsage = availRam

            listOf(
                MemoryUser(context.getString(R.string.memory_user_apps), appsUsage),
                MemoryUser(context.getString(R.string.memory_user_cache), cacheUsage),
                MemoryUser(context.getString(R.string.memory_user_system_and_reserved), systemAndReservedUsage),
                MemoryUser(context.getString(R.string.memory_user_free), freeUsage)
            )
        }

        return MemoryInfo(
            name = context.getString(R.string.home_storage_item_ram_title),
            type = "RAM",
            total = totalRam,
            used = usedRam,
            users = users
        )
    }

    private fun getInternalStorageInfo(): MemoryInfo {
        var totalStorage = 0L
        var freeStorage = 0L

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                totalStorage = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
                freeStorage = storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT)
            } catch (e: Exception) {
                Log.e("StorageDetailsRepository", "Error reading StorageStatsManager: ${e.message}")
            }
        }

        val dataStats = StatFs(Environment.getDataDirectory().path)
        val totalDataBytes = dataStats.blockCountLong * dataStats.blockSizeLong
        val freeDataBytes = dataStats.availableBlocksLong * dataStats.blockSizeLong
        val usedDataBytes = totalDataBytes - freeDataBytes

        if (totalStorage <= 0L) {
            totalStorage = getAdvertisedStorageInBytes(totalDataBytes)
        }
        if (freeStorage <= 0L) {
            freeStorage = freeDataBytes
        }

        val appsAndDataBytes = usedDataBytes.coerceAtMost(totalStorage - freeStorage)
        val systemAndReservedBytes = (totalStorage - (appsAndDataBytes + freeStorage)).coerceAtLeast(0L)

        val users = listOf(
            MemoryUser(context.getString(R.string.memory_user_apps_and_data), appsAndDataBytes),
            MemoryUser(context.getString(R.string.memory_user_system_and_reserved), systemAndReservedBytes),
            MemoryUser(context.getString(R.string.memory_user_free), freeStorage)
        )

        return MemoryInfo(
            name = context.getString(R.string.home_storage_item_internal_title),
            type = "Internal Storage",
            total = totalStorage,
            used = totalStorage - freeStorage,
            users = users
        )
    }

    private fun getAdvertisedStorageInBytes(usableBytes: Long): Long {
        val usableGb = usableBytes / (1024.0 * 1024.0 * 1024.0)
        val standardSizesGb = intArrayOf(8, 16, 32, 64, 128, 256, 512, 1024, 2048)
        val totalGb = standardSizesGb.firstOrNull { it >= usableGb } ?: kotlin.math.ceil(usableGb).toInt()
        return totalGb * 1024L * 1024L * 1024L
    }

    private fun getExternalStorageInfo(): MemoryInfo? {
        return try {
            val externalDirs = context.getExternalFilesDirs(null)
            if (externalDirs.size > 1 && externalDirs[1] != null) {
                val sdCardDir = externalDirs[1]
                if (Environment.isExternalStorageRemovable(sdCardDir) || Environment.getExternalStorageState(sdCardDir) == Environment.MEDIA_MOUNTED) {
                    val stat = StatFs(sdCardDir.path)
                    val totalBytes = stat.blockCountLong * stat.blockSizeLong
                    val availBytes = stat.availableBlocksLong * stat.blockSizeLong
                    val usedBytes = totalBytes - availBytes

                    MemoryInfo(
                        name = context.getString(R.string.memory_user_external_storage),
                        type = "External Storage",
                        total = totalBytes,
                        used = usedBytes,
                        users = listOf(
                            MemoryUser(context.getString(R.string.memory_user_used_space), usedBytes),
                            MemoryUser(context.getString(R.string.memory_user_free), availBytes)
                        )
                    )
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun parseProcMemInfo(): Map<String, Long> {
        val map = mutableMapOf<String, Long>()
        try {
            val file = RandomAccessFile("/proc/meminfo", "r")
            var line: String? = file.readLine()
            val pattern = Pattern.compile("(\\w+):\\s+(\\d+)\\s+kB")

            while (line != null) {
                val matcher = pattern.matcher(line)
                if (matcher.find()) {
                    val key = matcher.group(1)
                    val valueKb = matcher.group(2)?.toLongOrNull() ?: 0L
                    if (key != null) {
                        map[key] = valueKb * 1024L
                    }
                }
                line = file.readLine()
            }
            file.close()
        } catch (e: Exception) {
            Log.e("StorageDetailsRepository", "Error reading /proc/meminfo: ${e.message}")
        }
        return map
    }

    // Retained for backward compatibility
    suspend fun getStorageDetails(): List<StorageDetails?> {
        val memoryList = getMemoryInfoList()
        val result = mutableListOf<StorageDetails?>()

        for (info in memoryList) {
            val totalInGb = info.total.toDouble() / (1024 * 1024 * 1024)
            val usedInGb = info.used.toDouble() / (1024 * 1024 * 1024)
            val formattedTotal = DecimalFormat("#.##").format(totalInGb)
            val formattedUsed = DecimalFormat("#.##").format(usedInGb)

            val subtitle = "${formattedUsed} GB / ${formattedTotal} GB"
            val value = "${DecimalFormat("#").format(info.usedPercentage)}%"

            result.add(
                StorageDetails(
                    title = info.name,
                    subtitle = subtitle,
                    value = value,
                    progress = info.usedPercentage
                )
            )
        }
        return result
    }
}
