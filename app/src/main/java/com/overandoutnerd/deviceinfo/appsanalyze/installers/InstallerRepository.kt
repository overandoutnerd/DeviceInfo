package com.overandoutnerd.deviceinfo.appsanalyze.installers

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeComponent
import com.overandoutnerd.deviceinfo.appsanalyze.logic.ApiLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InstallerRepository(private val context: Context) {

    suspend fun getDetails(): List<AnalyzeComponent<String>> {
        return withContext(Dispatchers.IO) {
            val installers: MutableList<AnalyzeComponent<String>> = mutableListOf()
            try {
                val installersMap = mutableMapOf<String, Int>()
                val packageManager = context.packageManager
                val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

                for (appInfo in packages) {
                    val installerPackageName = try {
                        packageManager.getInstallerPackageName(appInfo.packageName)
                    } catch (e: Exception) {
                        null
                    }

                    val label = when {
                        installerPackageName == "com.android.shell" -> context.getString(com.overandoutnerd.deviceinfo.R.string.installer_debug)
                        installerPackageName != null -> {
                            try {
                                val installerInfo = packageManager.getPackageInfo(installerPackageName, 0)
                                installerInfo.applicationInfo?.loadLabel(packageManager).toString()
                            } catch (e: Exception) {
                                installerPackageName
                            }
                        }
                        (appInfo.applicationInfo?.flags ?: 0) and android.content.pm.ApplicationInfo.FLAG_SYSTEM != 0 -> {
                            context.getString(com.overandoutnerd.deviceinfo.R.string.installer_pre_installed)
                        }
                        else -> context.getString(com.overandoutnerd.deviceinfo.R.string.installer_direct_install)
                    }

                    val key = when {
                        installerPackageName == "com.android.shell" -> "debug"
                        installerPackageName != null -> installerPackageName
                        (appInfo.applicationInfo?.flags ?: 0) and android.content.pm.ApplicationInfo.FLAG_SYSTEM != 0 -> "system"
                        else -> "direct"
                    }
                    installersMap[key] = installersMap.getOrDefault(key, 0) + 1
                }
                val totalApps = packages.size

                for ((installer, appsInstalled) in installersMap) {
                    val icon = when (installer) {
                        "system" -> context.getDrawable(com.overandoutnerd.deviceinfo.R.drawable.ic_android_filled)
                        "debug" -> context.getDrawable(com.overandoutnerd.deviceinfo.R.drawable.baseline_developer_mode_24)
                        "direct" -> context.getDrawable(com.overandoutnerd.deviceinfo.R.drawable.ic_empty_circle_outlined)
                        else -> {
                            try {
                                packageManager.getApplicationIcon(installer)
                            } catch (e: Exception) {
                                context.getDrawable(com.overandoutnerd.deviceinfo.R.drawable.ic_android_filled)
                            }
                        }
                    } ?: context.getDrawable(com.overandoutnerd.deviceinfo.R.drawable.ic_android_filled)!!

                    val percentage = (appsInstalled.toDouble()/totalApps.toDouble()) * 100
                    val appName = ApiLevel.getAppName(context, installer)
                    installers.add(
                        AnalyzeComponent(
                            id = installer,
                            title = appName,
                            icon = icon,
                            appCount = appsInstalled,
                            percentage = percentage.toFloat()
                        )
                    )
                }

                installers.sortedByDescending { it.appCount }
            } catch (e: Exception) {
                e.message?.let { Log.e("Installer Repository", it) }
                installers
            }
        }
    }

}