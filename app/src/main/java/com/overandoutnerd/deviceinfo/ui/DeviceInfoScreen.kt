package com.overandoutnerd.deviceinfo.ui

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.overandoutnerd.deviceinfo.R
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeComponentsViewModel
import com.overandoutnerd.deviceinfo.appsanalyze.AppsAnalyzeScreen
import com.overandoutnerd.deviceinfo.appsanalyze.GlassAppsBottomSheet
import com.overandoutnerd.deviceinfo.digitalwellbeing.WellbeingScreen
import com.overandoutnerd.deviceinfo.home.ContextViewModelFactory
import com.overandoutnerd.deviceinfo.home.DeviceInfoHome
import com.overandoutnerd.deviceinfo.home.apps.SimpleAppDetails
import com.overandoutnerd.deviceinfo.home.sensors.SimpleSensorDetails
import com.overandoutnerd.deviceinfo.settings.SettingsScreen
import com.overandoutnerd.deviceinfo.settings.SettingsViewModel
import com.overandoutnerd.deviceinfo.settings.ThemeViewModel
import com.overandoutnerd.deviceinfo.settings.AppTheme
import com.overandoutnerd.deviceinfo.settings.GlassThemeChooserDialog
import com.overandoutnerd.deviceinfo.ui.components.LiquidBottomTab
import com.overandoutnerd.deviceinfo.ui.components.LiquidBottomTabs
import com.overandoutnerd.deviceinfo.ui.components.SplashScreen
import com.overandoutnerd.deviceinfo.ui.theme.LocalIsDarkTheme
import com.overandoutnerd.deviceinfo.ui.theme.healthBackgroundBottom
import com.overandoutnerd.deviceinfo.ui.theme.healthBackgroundBottomDark
import com.overandoutnerd.deviceinfo.ui.theme.healthBackgroundTop
import com.overandoutnerd.deviceinfo.ui.theme.healthBackgroundTopDark
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceInfoScreen(
    context: Context,
    settingsViewModel: SettingsViewModel,
    themeViewModel: ThemeViewModel,
    selectedTheme: AppTheme,
    navigateToAppDetails: (SimpleAppDetails) -> Unit,
    navigateToSensorDetails: (SimpleSensorDetails) -> Unit,
    hasUsageStatsPermission: (Boolean) -> Boolean,
    onClickSettingItem: (String) -> Unit
){
    val viewModel: MainViewModel = viewModel()

    val isDarkTheme = LocalIsDarkTheme.current
    val screenGradientTop = if (isDarkTheme) healthBackgroundTopDark else healthBackgroundTop
    val screenGradientBottom = if (isDarkTheme) healthBackgroundBottomDark else healthBackgroundBottom

    var showThemeDialog by remember { mutableStateOf(false) }
    val themeSettingFlag = stringResource(id = R.string.settings_item_flag_theme)
    val wrappedOnClickSettingItem: (String) -> Unit = { flag ->
        if (flag == themeSettingFlag) {
            showThemeDialog = true
        } else {
            onClickSettingItem(flag)
        }
    }

    val analyzeViewModel: AnalyzeComponentsViewModel = viewModel(factory = ContextViewModelFactory(context))
    val appsSheetState by analyzeViewModel.appsSheetState

    val selectedNavItem by viewModel.selectedNavItem
    val showSplashScreen by viewModel.showSplashScreen

    val title = viewModel.bottomNavigationItemsState.value[selectedNavItem].appBarTitle

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
    LaunchedEffect(selectedNavItem) {
        barScrollOffsetPx = 0f
    }

    Box(modifier = Modifier.fillMaxSize()) {


        val backgroundColor = Color.White
        val backdrop = rememberLayerBackdrop {
            drawRect(backgroundColor)
            drawContent()
        }

        Scaffold(
            modifier = Modifier
                .layerBackdrop(backdrop)
                .nestedScroll(barScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        if(viewModel.bottomNavigationItemsState.value[selectedNavItem] == viewModel.bottomNavigationItemsState.value[0]) {
                            Image(
                                painter = painterResource(id = viewModel.logo.value),
                                contentDescription = stringResource(
                                    id = viewModel.bottomNavigationItemsState.value[selectedNavItem].appBarTitle
                                ),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                                modifier = Modifier.size(162.dp, 25.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(id = viewModel.bottomNavigationItemsState.value[selectedNavItem].appBarTitle)
                            )
                        }

                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = screenGradientTop.copy(alpha = lerp(1f, 0.05f, barScrollFraction)),
                        scrolledContainerColor = screenGradientTop.copy(alpha = lerp(1f, 0.05f, barScrollFraction))
                    )
                )
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(screenGradientTop, screenGradientBottom)
                        )
                    )
                    .padding(it)
            ) {
                when(selectedNavItem){
                    0 -> {
                        DeviceInfoHome(
                            context = context,
                            navigateToAppDetails = navigateToAppDetails,
                            navigateToSensorDetails = navigateToSensorDetails,
                            scrollFraction = barScrollFraction
                        )
                    }
                    1 -> {
                        AppsAnalyzeScreen(context = context, backdrop = backdrop, scrollFraction = barScrollFraction)
                    }
//                    2 -> {
//                        WellbeingScreen(context, hasUsageStatsPermission)
//                    }
                    2 -> {
                        SettingsScreen(wrappedOnClickSettingItem, settingsViewModel)
                    }
                }
            }
        }

        Box(
            Modifier
                .safeContentPadding()
                .height(64f.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            LiquidBottomTabs(
                selectedTabIndex = { selectedNavItem },
                onTabSelected = { viewModel.setSelectedNavItem(it) },
                backdrop = backdrop,
                tabsCount = viewModel.bottomNavigationItemsState.value.size
            ) {
                viewModel.bottomNavigationItemsState.value.forEachIndexed { index, item ->
                    LiquidBottomTab(
                        onClick = { viewModel.setSelectedNavItem(index) }

                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (selectedNavItem == index)
                                    item.selectedIcon
                                else
                                    item.unselectedIcon
                            ),
                            modifier = Modifier.size(24.dp),
                            contentDescription = ""
                        )
                        Text(
                            text = stringResource(id = item.navBarTitle),
                            fontSize = MaterialTheme.typography.labelSmall.fontSize
                        )
                    }
                }
            }
        }

        SplashScreen(
            show = showSplashScreen,
            onDismiss = { viewModel.dismissSplashScreen() }
        )

        GlassAppsBottomSheet(
            backdrop = backdrop,
            state = appsSheetState,
            onDismiss = { analyzeViewModel.dismissAppsSheet() }
        )

        GlassThemeChooserDialog(
            backdrop = backdrop,
            visible = showThemeDialog,
            viewModel = themeViewModel,
            selectedTheme = selectedTheme,
            onDismiss = { showThemeDialog = false }
        )
    }
}

