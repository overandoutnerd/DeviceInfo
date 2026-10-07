package com.overandoutnerd.deviceinfo.ui.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp

/**
 * Height of the floating bottom pill ([com.overandoutnerd.deviceinfo.ui.components.LiquidBottomTabs])
 * itself. Kept in one place so every screen that needs to leave room for it agrees on the same
 * number as [com.overandoutnerd.deviceinfo.ui.DeviceInfoScreen].
 */
val BottomBarHeight = 64.dp

/** Some space between the last scrolled item and the floating bottom pill. */
val BottomBarSpacing = 16.dp

/**
 * The padding every scrollable screen hosted inside
 * [com.overandoutnerd.deviceinfo.ui.DeviceInfoScreen] should feed into its list's
 * `contentPadding` (LazyColumn).
 *
 * The screens themselves are laid out edge-to-edge (`fillMaxSize`, no clipping) so that as the
 * user scrolls, list content passes *behind* the translucent app bar / category tab row / bottom
 * pill instead of stopping short of them. `contentPadding` reserves just enough space so the
 * first and last items still land clear of the bars when the list is at rest, without ever
 * constraining what the list is allowed to draw behind.
 */
val LocalScreenContentPadding = compositionLocalOf { PaddingValues(0.dp) }
