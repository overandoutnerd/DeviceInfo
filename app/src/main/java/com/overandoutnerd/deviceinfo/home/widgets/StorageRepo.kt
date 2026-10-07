package com.overandoutnerd.deviceinfo.home.widgets

import android.content.Context
import android.os.StatFs
import android.util.Log
import com.overandoutnerd.deviceinfo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import kotlin.random.Random

object StorageRepo {

    suspend fun getStorageInfo(
        context: Context,
        delay: Long = Random.nextInt(1, 3) * 100L
    ): StorageInfo {

        if(delay > 0) {
            delay(delay)
        }

        return withContext(Dispatchers.IO) {

            var storageInfo: StorageInfo = StorageInfo.Available()
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
                storageInfo = StorageInfo.Available(
                    formattedTotal,
                    formattedFree,
                    progress,
                    value
                )
                storageInfo
            } catch (e: Exception) {
                Log.e("Storage Repo", "getStorageInfo ${e.message}")
                storageInfo
            }
        }

    }

}