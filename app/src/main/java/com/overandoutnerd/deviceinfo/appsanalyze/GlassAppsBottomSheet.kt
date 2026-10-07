package com.overandoutnerd.deviceinfo.appsanalyze

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.ui.theme.LocalIsDarkTheme
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import kotlin.math.roundToInt

@Composable
fun BoxScope.GlassAppsBottomSheet(
    backdrop: Backdrop,
    state: AnalyzeAppsSheetState,
    onDismiss: () -> Unit
) {
    val isDarkTheme = LocalIsDarkTheme.current
    val sheetSurfaceTint = if (isDarkTheme) {
        Color(0xFF1C232D).copy(alpha = 0.6f)
    } else {
        Color.White.copy(alpha = 0.5f)
    }
    val handleColor = if (isDarkTheme) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.25f)

    AnimatedVisibility(
        visible = state.visible,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(200)),
        modifier = Modifier.matchParentSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )
    }

    AnimatedVisibility(
        visible = state.visible,
        enter = slideInVertically(tween(320)) { it } + fadeIn(tween(320)),
        exit = slideOutVertically(tween(220)) { it } + fadeOut(tween(220)),
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        val density = LocalDensity.current
        val dismissThresholdPx = with(density) { 88.dp.toPx() }
        var dragOffsetPx by remember { mutableFloatStateOf(0f) }

        Column(
            Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, dragOffsetPx.roundToInt()) }
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp) },
                    effects = {
                        vibrancy()
                        blur(7f.dp.toPx())
                        lens(24f.dp.toPx(), 48f.dp.toPx(), true)
                    },
                    onDrawSurface = { drawRect(sheetSurfaceTint) }
                )
                .navigationBarsPadding()
                // Consume every touch that lands on the sheet itself so it
                // never falls through to the scrim behind it - only the
                // drag handle (below) and the scrim outside the sheet
                // should be able to dismiss it.
                .pointerInput(Unit) {
                    detectTapGestures { }
                }
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(handleColor)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetPx = (dragOffsetPx + dragAmount).coerceAtLeast(0f)
                            },
                            onDragEnd = {
                                if (dragOffsetPx > dismissThresholdPx) {
                                    onDismiss()
                                } else {
                                    dragOffsetPx = 0f
                                }
                            },
                            onDragCancel = { dragOffsetPx = 0f }
                        )
                    }
            )

            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            ) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (state.loading) {
                        stringResource(id = R.string.analyze_apps_sheet_loading)
                    } else {
                        pluralStringResource(
                            id = R.plurals.analyze_apps_sheet_count,
                            count = state.apps.size,
                            state.apps.size
                        )
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                when {
                    state.loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    state.error != null -> {
                        Text(
                            text = stringResource(id = R.string.analyze_apps_sheet_error),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                    state.apps.isEmpty() -> {
                        Text(
                            text = stringResource(id = R.string.analyze_apps_sheet_empty),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 420.dp)
                        ) {
                            items(state.apps, key = { it.packageName }) { app ->
                                GlassAnalyzeAppRow(app)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassAnalyzeAppRow(app: AnalyzeAppInfo) {
    val logo = remember(app.packageName) {
        app.icon.toBitmap(width = 96, height = 96).asImageBitmap()
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = logo,
            contentDescription = null,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = app.appName,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = app.packageName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (app.versionName != null) {
            Text(
                text = "v${app.versionName}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
