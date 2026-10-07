package com.overandoutnerd.deviceinfo.home.storage

import java.text.DecimalFormat

data class MemoryInfo(
    val name: String,
    val type: String,
    val total: Long,
    val used: Long,
    val users: List<MemoryUser>
) {
    val usedPercentage: Float
        get() = if (total > 0) ((used.toFloat() / total.toFloat()) * 100f).coerceIn(0f, 100f) else 0f

    val formattedTotal: String
        get() = formatBytes(total)

    val formattedUsed: String
        get() = formatBytes(used)

    val formattedAvailable: String
        get() = formatBytes((total - used).coerceAtLeast(0L))
}

data class MemoryUser(
    val name: String,
    val usage: Long
) {
    val formattedUsage: String
        get() = formatBytes(usage)

    fun getPercentage(total: Long): Float {
        return if (total > 0) ((usage.toFloat() / total.toFloat()) * 100f).coerceIn(0f, 100f) else 0f
    }
}

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    val value = bytes / Math.pow(1024.0, digitGroups.toDouble())
    val format = DecimalFormat("#.##")
    return "${format.format(value)} ${units[digitGroups.coerceAtMost(units.size - 1)]}"
}
