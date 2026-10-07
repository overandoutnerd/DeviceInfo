package com.overandoutnerd.deviceinfo.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Extra bottom inset every top-level tab's scrollable content should reserve
 * so its last item isn't hidden behind the floating [com.overandoutnerd.deviceinfo.ui.components.LiquidBottomTabs]
 * bar, which floats above the screen rather than participating in Scaffold's
 * own content padding.
 */
val BottomBarContentClearance = 110.dp
