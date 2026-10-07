package com.overandoutnerd.deviceinfo.ui.components

import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.TabPosition
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp

fun TabIndicatorScope.pagerTabIndicatorOffset(
    modifier: Modifier,
    pagerState: PagerState,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier = modifier.tabIndicatorLayout { measurable, constraints, tabPositions ->
    val stateBridge = object : PagerStateBridge {
        override val currentPage: Int
            get() = pagerState.currentPage
        override val currentPageOffset: Float
            get() = pagerState.currentPageOffsetFraction
    }

    if (tabPositions.isEmpty()) {
        // If there are no pages, nothing to show
        layout(constraints.maxWidth, 0) {}
    } else {
        val currentPage = minOf(tabPositions.lastIndex, pageIndexMapping(stateBridge.currentPage))
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = stateBridge.currentPageOffset
        val indicatorWidth = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.contentWidth, nextTab.contentWidth, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.contentWidth, previousTab.contentWidth, -fraction).roundToPx()
        } else {
            currentTab.contentWidth.roundToPx()
        }
        val indicatorOffset = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.left, nextTab.left, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.left, previousTab.left, -fraction).roundToPx()
        } else {
            currentTab.left.roundToPx()
        }
        val placeable = measurable.measure(
            Constraints(
                minWidth = indicatorWidth,
                maxWidth = indicatorWidth,
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )
        layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
            placeable.placeRelative(
                indicatorOffset,
                maxOf(constraints.minHeight - placeable.height, 0)
            )
        }
    }
}

fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier {
    val stateBridge = object : PagerStateBridge {
        override val currentPage: Int
            get() = pagerState.currentPage
        override val currentPageOffset: Float
            get() = pagerState.currentPageOffsetFraction
    }

    return pagerTabIndicatorOffset(stateBridge, tabPositions, pageIndexMapping)
}

private fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerStateBridge,
    tabPositions: List<TabPosition>,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier = layout { measurable, constraints ->
    if (tabPositions.isEmpty()) {
        // If there are no pages, nothing to show
        layout(constraints.maxWidth, 0) {}
    } else {
        val currentPage = minOf(tabPositions.lastIndex, pageIndexMapping(pagerState.currentPage))
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = pagerState.currentPageOffset
        val indicatorWidth = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.contentWidth, nextTab.contentWidth, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.contentWidth, previousTab.contentWidth, -fraction).roundToPx()
        } else {
            currentTab.contentWidth.roundToPx()
        }
        val indicatorOffset = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.left, nextTab.left, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.left, previousTab.left, -fraction).roundToPx()
        } else {
            currentTab.left.roundToPx()
        }
        val placeable = measurable.measure(
            Constraints(
                minWidth = indicatorWidth,
                maxWidth = indicatorWidth,
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )
        layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
            placeable.placeRelative(
                indicatorOffset,
                maxOf(constraints.minHeight - placeable.height, 0)
            )
        }
    }
}

internal interface PagerStateBridge {
    val currentPage: Int
    val currentPageOffset: Float
}