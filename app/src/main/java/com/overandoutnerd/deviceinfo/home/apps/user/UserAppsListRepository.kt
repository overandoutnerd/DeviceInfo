package com.overandoutnerd.deviceinfo.home.apps.user

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.overandoutnerd.deviceinfo.home.apps.SimpleAppDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserAppsListRepository(private val context: Context) {
    suspend fun getUserInstalledApps(): List<SimpleAppDetails>{
        return withContext(Dispatchers.IO){
            val packageManager: PackageManager = context.packageManager
            val appsList: MutableList<SimpleAppDetails> = mutableListOf()
            try {
                val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                for (appInfo in installedApps){
                    if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0){
                        val packageName = appInfo.packageName
                        val appName = appInfo.loadLabel(packageManager).toString()
                        val appIcon = appInfo.loadIcon(packageManager)
                        val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                        val app = SimpleAppDetails(packageName, appName, appIcon, isSystemApp)
                        appsList.add(app)
                    }
                }
                appsList.sortedBy { it.name }
            } catch (e: Exception){
                Log.e("SimpleAppDetailsRepository", e.message!!)
                mutableListOf()
            }
        }
    }
}