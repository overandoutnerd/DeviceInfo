package com.overandoutnerd.deviceinfo.appsanalyze.logic

import android.content.Context
import android.os.Build
import android.util.Log
import com.overandoutnerd.deviceinfo.R

enum class SdkType {
    SDK_TARGET,
    SDK_MINIMUM,
    NOT_SDK
}

object ApiLevel {
    /** Newest API level represented by the app's status colors and demo data. */
    const val LATEST_TARGET = 37
    const val LATEST_MINIMUM = 34

    fun targetMinus(versionsBehind: Int): Int {
        require(versionsBehind >= 0) { "versionsBehind must not be negative" }
        return (LATEST_TARGET - versionsBehind).coerceAtLeast(1)
    }

    fun minimumMinus(versionsBehind: Int): Int {
        require(versionsBehind >= 0) { "versionsBehind must not be negative" }
        return (LATEST_MINIMUM - versionsBehind).coerceAtLeast(1)
    }

    /** ARGB without alpha channel bits; callers add alpha if needed. */
    fun <T> colorArgb(api: T, sdkType: SdkType): Long =
        when(api){
            is Int -> {
                when(sdkType) {
                    SdkType.SDK_TARGET -> {
                        when {
                            api <= targetMinus(4) -> 0xFFD31B33
                            api == targetMinus(3) -> 0xFFE54B4B
                            api == targetMinus(2) -> 0xFFE37A46
                            api == targetMinus(1) -> 0xFF178E96
                            else -> 0xFF14B572
                        }
                    }
                    SdkType.SDK_MINIMUM -> {
                        when {
                            api <= minimumMinus(4) -> 0xFFD31B33
                            api == minimumMinus(3) -> 0xFFE54B4B
                            api == minimumMinus(2) -> 0xFFE37A46
                            api == minimumMinus(1) -> 0xFF178E96
                            else -> 0xFF14B572
                        }
                    }

                    SdkType.NOT_SDK -> 0xFF2E9BFA
                }
            }
            is String -> {
                when (api) {
                    "system" -> 0xFF3DDC84 // Android Green
                    "debug" -> 0xFFF44336 // Red
                    "direct" -> 0xFF9E9E9E // Grey
                    "com.android.vending" -> 0xFF4285F4 // Google Blue
                    "com.amazon.venezia" -> 0xFFFF9900 // Amazon Orange
                    "com.sec.android.app.samsungapps" -> 0xFF034EA2 // Samsung Blue
                    "com.huawei.appmarket" -> 0xFFC7000B // Huawei Red
                    "com.xiaomi.mipicks" -> 0xFFFF6700 // Xiaomi Orange
                    else -> {
                        // Generate a deterministic color based on the string hash
                        val hash = api.hashCode()
                        val r = (hash and 0xFF0000 shr 16)
                        val g = (hash and 0x00FF00 shr 8)
                        val b = (hash and 0x0000FF)
                        // Ensure it's not too dark or too light for visibility
                        val finalR = (r * 0.8 + 50).toInt().coerceIn(0, 255)
                        val finalG = (g * 0.8 + 50).toInt().coerceIn(0, 255)
                        val finalB = (b * 0.8 + 50).toInt().coerceIn(0, 255)
                        (0xFF shl 24 or (finalR shl 16) or (finalG shl 8) or finalB).toLong()
                    }
                }
            }
            else -> 0xFF2E9BFA
        }

    fun getAndroidVersionName(api: Int, context: Context): String {
        var fullVersionName: String = ""
        val androidTitle = context.getString(R.string.android_system_title)
        val versionName = androidTitle + when (api) {
            1 -> " 1.0"
            2 -> " 1.1"
            3 -> " 1.5"
            4 -> " 1.6"
            5 -> " 2.0"
            6 -> " 2.0.1"
            7 -> " 2.1"
            8 -> " 2.2"
            9 -> " 2.3"
            10 -> " 2.3.3"
            11 -> " 3.0"
            12 -> " 3.1"
            13 -> " 3.2"
            14 -> " 4.0"
            15 -> " 4.0.3"
            16 -> " 4.1"
            17 -> " 4.2"
            18 -> " 4.3"
            19 -> " 4.4"
            20 -> " 4.4W"
            21 -> " 5.0"
            22 -> " 5.1"
            23 -> " 6"
            24 -> " 7"
            25 -> " 7.1"
            26 -> " 8"
            27 -> " 8.1"
            28 -> " 9"
            29 -> " 10"
            30 -> " 11"
            31 -> " 12"
            32 -> " 12L"
            33 -> " 13"
            34 -> " 14"
            35 -> " 15"
            36 -> " 16"
            37 -> " 17"
            else -> "Unknown"
        }
        try {
            val versionCode = Build.VERSION_CODES::class.java.fields[api].name.replace("_", " ")
            fullVersionName = "$versionName - $versionCode"
        } catch (e: Exception) {
            Log.e("Target API Screen", "${e.message}")
            fullVersionName = versionName
        }
        return fullVersionName
    }

    fun getAppName(context: Context, packageName: String): String {
        return when (packageName) {
            "system" -> context.getString(R.string.installer_pre_installed)
            "debug" -> context.getString(R.string.installer_debug)
            "direct" -> context.getString(R.string.installer_direct_install)
            else -> {
                try {
                    context.packageManager.getPackageInfo(packageName, 0).applicationInfo?.loadLabel(context.packageManager).toString()
                } catch (e: Exception) {
                    packageName
                }
            }
        }
    }
}