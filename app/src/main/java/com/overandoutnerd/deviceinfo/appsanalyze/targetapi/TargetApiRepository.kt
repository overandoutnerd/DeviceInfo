package com.overandoutnerd.deviceinfo.appsanalyze.targetapi

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeComponent
import com.overandoutnerd.deviceinfo.appsanalyze.logic.ApiLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TargetApiRepository(private val context: Context) {

    suspend fun getDetails(): List<AnalyzeComponent<Int>> {
        return withContext(Dispatchers.IO){
            val targetApiList: MutableList<AnalyzeComponent<Int>> = mutableListOf()
            try {
                val packageManager = context.packageManager
                val installedPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
                val totalApps = installedPackages.size

                val apiCountMap = mutableMapOf<Int, Int>()
                for(packageInfo in installedPackages){
                    val targetSdkVersion = packageInfo.applicationInfo?.targetSdkVersion ?: 0
                    apiCountMap[targetSdkVersion] = (apiCountMap[targetSdkVersion] ?: 0) + 1
                }
                for ((api, count) in apiCountMap) {
                    val percentage = (count.toDouble()/totalApps.toDouble()) * 100
                    val title = ApiLevel.getAndroidVersionName(api, context)
                    val sdk = AnalyzeComponent(id = api, title= title, appCount = count, percentage =  percentage.toFloat())
                    targetApiList.add(sdk)
                    Log.e("TargetApiRepository", "Version: ${sdk.id}, Count: ${sdk.appCount}, Percentage: ${sdk.percentage}")
                }
                targetApiList.sortedByDescending { it.id }
            } catch (e: Exception) {
                Log.e("TargetApiRepository", "Failed to retrieve data: ${e.message}")
                targetApiList
            }
        }
    }

}