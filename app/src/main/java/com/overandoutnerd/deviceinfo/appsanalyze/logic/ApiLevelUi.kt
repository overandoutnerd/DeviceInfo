package com.overandoutnerd.deviceinfo.appsanalyze.logic

import android.content.Context
import androidx.compose.ui.graphics.Color

fun <T> T.apiToComposeColor(sdkType: SdkType): Color = Color(ApiLevel.colorArgb(this, sdkType))

fun Int.apiToVersionName(context: Context): String = ApiLevel.getAndroidVersionName(this, context)