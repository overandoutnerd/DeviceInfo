package com.overandoutnerd.deviceinfo.home.apps.details

import android.graphics.drawable.Drawable
import java.util.Date

data class AppDetails(
    var packageName: String,
    var name: String,
    var icon: Drawable?,
    var version: String,
    var minSdk: Int,
    var targetSdk: Int,
    var installDate: Date,
    var lastUpdate: Date,
    var isSystemApp: Boolean,
    var totalPermissions: Int,
    var totalProviders: Int,
    var totalReceivers: Int,
    var totalServices: Int,
    var onClickOpen: () -> Unit,
    var onClickStore: () -> Unit,
    var onClickUninstall: () -> Unit,
)
