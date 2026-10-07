package com.overandoutnerd.deviceinfo.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.ui.graphics.vector.ImageVector

data class TabItem(
    @StringRes val title: Int,
    val name: String,
    val icon: ImageVector = Icons.Rounded.Circle
)
