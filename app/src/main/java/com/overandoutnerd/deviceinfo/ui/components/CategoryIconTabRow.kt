package com.overandoutnerd.deviceinfo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * A horizontally scrollable row of icon + title pill tabs housed inside a
 * single rounded capsule "track".
 *
 * Unlike a plain per-chip background swap, this draws a single sliding
 * "thumb" behind the chips that animates both its position and its width as
 * the selection changes - whether that change comes from tapping a chip or
 * from swiping the paired pager. The track also auto-scrolls so the selected
 * chip is always brought into view.
 *
 * @param itemCount Total number of tabs
 * @param selectedIndex Currently selected tab index (drives chip tint + auto-scroll)
 * @param onTabSelected Called with the tapped index
 * @param icon Returns the [ImageVector] to draw for a given index
 * @param title Returns the label text to draw for a given index
 * @param contentDescription Returns the accessibility label for a given index
 * @param currentPageOffset Continuous tab position (e.g. `pagerState.currentPage + pagerState.currentPageOffsetFraction`)
 *  used to slide/resize the indicator in lockstep with a swipe. Defaults to the
 *  (non-continuous) [selectedIndex] when no pager is available.
 * @param scrollFraction 0f.1f - how far the paired content has been scrolled away
 *  from the top. Used to fade the track's background toward transparent.
 */
@Composable
fun CategoryIconTabRow(
    itemCount: Int,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    icon: (Int) -> ImageVector,
    title: @Composable (Int) -> String,
    contentDescription: @Composable (Int) -> String,
    modifier: Modifier = Modifier,
    currentPageOffset: () -> Float = { selectedIndex.toFloat() },
    scrollFraction: Float = 0f
) {
    if (itemCount <= 0) return

    val density = LocalDensity.current
    val scrollState = rememberScrollState()

    val trackAlpha = lerp(1f, 0.06f, scrollFraction)
    val trackColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = trackAlpha)

    // Measured (left, width) in px for every tab, relative to the scrollable track.
    val tabBounds = remember { mutableStateMapOf<Int, Pair<Float, Float>>() }

    BoxWithConstraints(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(trackColor)
    ) {
        val viewportWidthPx = with(density) { maxWidth.toPx() }

        val continuousPosition = currentPageOffset()
            .coerceIn(0f, (itemCount - 1).toFloat())
        val flooredIndex = floor(continuousPosition).toInt().coerceIn(0, itemCount - 1)
        val ceilIndex = (flooredIndex + 1).coerceAtMost(itemCount - 1)
        val fraction = continuousPosition - flooredIndex

        val startBounds = tabBounds[flooredIndex]
        val endBounds = tabBounds[ceilIndex]

        val indicatorLeftPx = when {
            startBounds != null && endBounds != null -> lerp(startBounds.first, endBounds.first, fraction)
            startBounds != null -> startBounds.first
            else -> 0f
        }
        val indicatorWidthPx = when {
            startBounds != null && endBounds != null -> lerp(startBounds.second, endBounds.second, fraction)
            startBounds != null -> startBounds.second
            else -> 0f
        }

        // Keep the selected chip in view by scrolling the track toward it.
        LaunchedEffect(selectedIndex, tabBounds[selectedIndex], viewportWidthPx) {
            val bounds = tabBounds[selectedIndex] ?: return@LaunchedEffect
            if (scrollState.maxValue <= 0) return@LaunchedEffect
            val target = (bounds.first + bounds.second / 2f - viewportWidthPx / 2f)
                .coerceIn(0f, scrollState.maxValue.toFloat())
            scrollState.animateScrollTo(target.roundToInt())
        }

        Box(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(6.dp)
        ) {
            if (indicatorWidthPx > 0f) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(indicatorLeftPx.roundToInt(), 0) }
                        .width(with(density) { indicatorWidthPx.toDp() })
                        .height(44.dp)
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(18.dp),
                            clip = false,
                            ambientColor = Color.Black.copy(alpha = 0.15f),
                            spotColor = Color.Black.copy(alpha = 0.15f)
                        )
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (index in 0 until itemCount) {
                    val selected = index == selectedIndex

                    val contentTint by animateColorAsState(
                        targetValue = if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        label = "categoryTabContentTint"
                    )

                    Row(
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                tabBounds[index] = coordinates.positionInParent().x to
                                        coordinates.size.width.toFloat()
                            }
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onTabSelected(index) }
                            .height(44.dp)
                            .padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon(index),
                            contentDescription = contentDescription(index),
                            tint = contentTint,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = title(index),
                            color = contentTint,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
