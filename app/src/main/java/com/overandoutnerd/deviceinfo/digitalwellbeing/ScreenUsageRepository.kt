package com.overandoutnerd.deviceinfo.digitalwellbeing

import android.app.Activity
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class WeeklyUsage(
    val weekScreenTime: Long,
    val weekDuration: String,
    val dailyUsages: List<DailyUsage>
)

data class DailyUsage(
    val date: String,
    val dayScreenTime: Long,
    val dayDuration: String,
    val usedApps: List<AppUsage>
)

data class AppUsage(
    val app: App,
    val appDuration: String,
    val appScreenTime: Long
)

data class App(
    val packageName: String,
    val name: String,
    val icon: Drawable? = null
)

const val REQUEST_USAGE_STATS_PERMISSION = 1001

class ScreenUsageRepository(private val context: Context) {
    suspend fun getMostUsedApps(): DailyUsage {
        return withContext(Dispatchers.IO) {
            try {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                val startTime = calendar.timeInMillis
                val endTime = System.currentTimeMillis()

                val usedAppsList = getUsageForInterval(startTime, endTime)
                val totalScreenTime = usedAppsList.sumOf { it.appScreenTime }
                val screenTimeDuration = getDuration(totalScreenTime)
                val date = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(startTime))

                DailyUsage(date, totalScreenTime, screenTimeDuration, usedAppsList)
            } catch (e: Exception) {
                Log.e("ScreenUsageRepository", "Error getting most used apps: ${e.message}")
                DailyUsage("", 0, "", emptyList())
            }
        }
    }

    suspend fun getWeeklyUsage(): WeeklyUsage {
        return withContext(Dispatchers.IO) {
            try {
                val dailyUsages = mutableListOf<DailyUsage>()
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                // Start from the first day of the current week
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

                val dateFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault())

                for (day in 0 until 7) {
                    val startTime = calendar.timeInMillis
                    val tempCalendar = calendar.clone() as Calendar
                    tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
                    val endTime = tempCalendar.timeInMillis

                    val actualEndTime = if (endTime > System.currentTimeMillis()) System.currentTimeMillis() else endTime

                    val usedAppsList = if (startTime > System.currentTimeMillis()) {
                        emptyList()
                    } else {
                        getUsageForInterval(startTime, actualEndTime)
                    }

                    val dayScreenTime = usedAppsList.sumOf { it.appScreenTime }

                    dailyUsages.add(DailyUsage(
                        date = dateFormat.format(Date(startTime)),
                        dayScreenTime = dayScreenTime,
                        dayDuration = getDuration(dayScreenTime),
                        usedApps = usedAppsList
                    ))

                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }

                val weekScreenTime = dailyUsages.sumOf { it.dayScreenTime }
                WeeklyUsage(weekScreenTime, getDuration(weekScreenTime), dailyUsages)
            } catch (e: Exception) {
                Log.e("ScreenUsageRepository", "Error getting weekly usage: ${e.message}")
                WeeklyUsage(0, "", emptyList())
            }
        }
    }

    private fun getUsageForInterval(startTime: Long, endTime: Long): List<AppUsage> {
        val pm = context.packageManager
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val aggregateStats = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)

        val launchers = getLauncherPackages()
        val selfPackage = context.packageName

        return aggregateStats.values
            .filter { it.totalTimeInForeground > 0 }
            .filter { it.packageName != selfPackage && !launchers.contains(it.packageName) }
            .filter { it.packageName != "android" && it.packageName != "com.android.systemui" }
            .map { usageStats ->
                val packageName = usageStats.packageName
                val totalScreenTime = usageStats.totalTimeInForeground
                val app = try {
                    val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
                    } else {
                        @Suppress("DEPRECATION")
                        pm.getPackageInfo(packageName, 0)
                    }
                    val appName = packageInfo.applicationInfo?.loadLabel(pm).toString()
                    val appIcon = packageInfo.applicationInfo?.loadIcon(pm)
                    App(packageName, appName, appIcon)
                } catch (e: Exception) {
                    App(packageName, "")
                }
                AppUsage(app, getDuration(totalScreenTime), totalScreenTime)
            }
            .sortedByDescending { it.appScreenTime }
    }

    private fun getLauncherPackages(): Set<String> {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        return resolveInfoList.map { it.activityInfo.packageName }.toSet()
    }


    private fun getDuration(milliseconds: Long): String {
        if (milliseconds < 0) return "No data"

        val totalSeconds = milliseconds / 1000
        val days = totalSeconds / (24 * 3600)
        val remainingAfterDays = totalSeconds % (24 * 3600)

        val hours = remainingAfterDays / 3600
        val remainingAfterHours = remainingAfterDays % 3600

        val minutes = remainingAfterHours / 60
        val remainingSeconds = remainingAfterHours % 60

        val durationParts = mutableListOf<String>()

        if (days > 0) durationParts.add("$days d")
        if (hours > 0) durationParts.add("$hours hrs")
        if (minutes > 0) durationParts.add("$minutes mins")
        if (remainingSeconds > 0 || durationParts.isEmpty()) durationParts.add("$remainingSeconds seconds")

        return durationParts.joinToString(" ")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun hasUsageStatsPermission(): Boolean {
        return try {
            val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    fun requestUsageStatsPermission(activity: Activity) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        activity.startActivityForResult(intent, REQUEST_USAGE_STATS_PERMISSION)
    }
}