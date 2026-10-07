package com.overandoutnerd.deviceinfo.settings

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.overandoutnerd.deviceinfo.ui.theme.BottomBarContentClearance
import com.overandoutnerd.deviceinfo.ui.theme.LocalIsDarkTheme
import com.overandoutnerd.deviceinfo.ui.theme.healthBackgroundBottom
import com.overandoutnerd.deviceinfo.ui.theme.healthBackgroundBottomDark
import com.overandoutnerd.deviceinfo.ui.theme.healthBackgroundTop
import com.overandoutnerd.deviceinfo.ui.theme.healthBackgroundTopDark

private data class LanguageOption(val tag: String?, val displayName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    settingsViewModel: SettingsViewModel,
    onBackPressed: () -> Unit
) {

    val isDarkTheme = LocalIsDarkTheme.current
    val screenGradientTop = if (isDarkTheme) healthBackgroundTopDark else healthBackgroundTop
    val screenGradientBottom = if (isDarkTheme) healthBackgroundBottomDark else healthBackgroundBottom

    val density = LocalDensity.current
    val maxBarScrollPx = with(density) { 96.dp.toPx() }
    var barScrollOffsetPx by remember { mutableFloatStateOf(0f) }
    val barScrollFraction by remember {
        derivedStateOf { (barScrollOffsetPx / maxBarScrollPx).coerceIn(0f, 1f) }
    }
    val barScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                barScrollOffsetPx = (barScrollOffsetPx - available.y).coerceIn(0f, maxBarScrollPx)
                return Offset.Zero
            }
        }
    }

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current
    val onBackPressedCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }
    DisposableEffect(backPressedDispatcher) {
        backPressedDispatcher!!.onBackPressedDispatcher.addCallback(onBackPressedCallback)
        onDispose {
            onBackPressedCallback.remove()
        }
    }

    // Only languages this app actually ships translations for (plus
    // "System default") - offering languages with no resources would just
    // silently fall back to English anyway.
    val options = listOf(
        LanguageOption(null, stringResource(id = R.string.language_option_system_default)),
        LanguageOption("en", stringResource(id = R.string.language_option_english)),
        LanguageOption("hi", stringResource(id = R.string.language_option_hindi))
    )
    val supportsInApp = settingsViewModel.supportsInAppLanguageSelection()
    var selectedTag by remember { mutableStateOf(settingsViewModel.getSelectedLanguageTag()) }

    val radioItems = options.mapIndexed { index, option ->
        RadioButtonItem(id = index, title = option.displayName)
    }
    val selectedId = options.indexOfFirst { it.tag == selectedTag }.coerceAtLeast(0)

    Scaffold(
        modifier = Modifier.nestedScroll(barScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.language_screen_app_bar_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = screenGradientTop.copy(alpha = lerp(1f, 0.05f, barScrollFraction)),
                    scrolledContainerColor = screenGradientTop.copy(alpha = lerp(1f, 0.05f, barScrollFraction))
                ),
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.content_description_go_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(screenGradientTop, screenGradientBottom)
                    )
                )
                .padding(padding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = BottomBarContentClearance)
            ) {
                if (!supportsInApp) {
                    item {
                        RoundedCornerBox {
                            Text(
                                text = stringResource(id = R.string.language_requires_android_13),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                item {
                    RoundedCornerBox {
                        Column {
                            RadioGroup(
                                items = radioItems,
                                selected = selectedId,
                                onItemSelect = { id ->
                                    val tag = options[id].tag
                                    selectedTag = tag
                                    settingsViewModel.setAppLanguage(tag)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
