package com.overandoutnerd.deviceinfo.home

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.overandoutnerd.deviceinfo.home.apps.InstalledAppsHomeScreen
import com.overandoutnerd.deviceinfo.home.apps.SimpleAppDetails
import com.overandoutnerd.deviceinfo.home.battery.BatteryDetailsScreen
import com.overandoutnerd.deviceinfo.home.battery.BatteryDetailsViewModel
import com.overandoutnerd.deviceinfo.home.camera.CameraDetailsScreen
import com.overandoutnerd.deviceinfo.home.dashboard.DashboardScreen
import com.overandoutnerd.deviceinfo.home.device.DeviceDetailsViewModel
import com.overandoutnerd.deviceinfo.home.display.DisplayDetailsViewModel
import com.overandoutnerd.deviceinfo.home.processor.ProcessorDetailsViewModel
import com.overandoutnerd.deviceinfo.home.sensors.SensorsScreen
import com.overandoutnerd.deviceinfo.home.sensors.SimpleSensorDetails
import com.overandoutnerd.deviceinfo.home.storage.StorageDetailsScreen
import com.overandoutnerd.deviceinfo.home.system.SystemDetailsViewModel
import com.overandoutnerd.deviceinfo.ui.components.CategoryIconTabRow
import com.overandoutnerd.deviceinfo.ui.components.DetailListItem
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.overandoutnerd.deviceinfo.ui.theme.BottomBarContentClearance

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeviceInfoHome(
    context: Context,
    navigateToAppDetails: (SimpleAppDetails) -> Unit,
    navigateToSensorDetails: (SimpleSensorDetails) -> Unit,
    scrollFraction: Float = 0f
){

    val viewModel: DeviceComponentsDetailsViewModel = viewModel(factory = ContextViewModelFactory(context))
    val deviceDetailsViewModel: DeviceDetailsViewModel = viewModel(factory = ContextViewModelFactory(context))
    val systemDetailsViewModel: SystemDetailsViewModel = viewModel(factory = ContextViewModelFactory(context))
    val processorDetailsViewModel: ProcessorDetailsViewModel = viewModel(factory = ContextViewModelFactory(context))
    val displayDetailsViewModel: DisplayDetailsViewModel = viewModel(factory = ContextViewModelFactory(context))

    val selectedTabItem by viewModel.selectedTabItem

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val pagerState = rememberPagerState {
            viewModel.tabItemsState.value.size
        }
        LaunchedEffect(selectedTabItem) {
            pagerState.animateScrollToPage(selectedTabItem)
        }
        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if(!pagerState.isScrollInProgress) {
                viewModel.updateSelectedTabItem(pagerState.currentPage)
            }
        }
        CategoryIconTabRow(
            itemCount = viewModel.tabItemsState.value.size,
            selectedIndex = selectedTabItem,
            onTabSelected = { viewModel.updateSelectedTabItem(it) },
            icon = { index -> viewModel.tabItemsState.value[index].icon },
            title = { index -> stringResource(id = viewModel.tabItemsState.value[index].title) },
            contentDescription = { index ->
                stringResource(id = viewModel.tabItemsState.value[index].title)
            },
            currentPageOffset = { pagerState.currentPage + pagerState.currentPageOffsetFraction },
            scrollFraction = scrollFraction
        )
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(state = pagerState) { index ->
                when(index) {
                    0 -> {
                        DashboardScreen(
                            context = context,
                            onClickItem = {
                                viewModel.updateSelectedTabItem(it)
                            }
                        )
                    }
                    1 -> {
                        InstalledAppsHomeScreen(
                            context = context,
                            navigateToAppDetails = navigateToAppDetails
                        )
                    }
                    2 -> {
                        StorageDetailsScreen(context = context)
                    }
                    3 -> {
                        DeviceComponentScreen(deviceDetailsViewModel.deviceDetailsState)
                    }
                    4 -> {
                        DeviceComponentScreen(systemDetailsViewModel.systemDetailsState)
                    }
                    5 -> {
                        DeviceComponentScreen(processorDetailsViewModel.processorDetailsState)
                    }
                    6 -> {
                        BatteryDetailsScreen(context)
                    }
                    7 -> {
                        DeviceComponentScreen(displayDetailsViewModel.displayDetailsState)
                    }
                    8 -> {
                        CameraDetailsScreen(context = context)
                    }
                    9 -> {
                        SensorsScreen(context, navigateToSensorDetails)
                    }
                    else -> {
                        DeviceComponentScreen(deviceDetailsViewModel.deviceDetailsState)
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceComponentScreen(detailsState: State<ComponentDetailsState>) {

    val state by detailsState

    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        when {
            state.loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            state.error != null -> Text(text = "Error Occurred")
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = BottomBarContentClearance)
                ) {
                    item {
                        RoundedCornerBox {
                            Column {
                                state.componentDetails.forEach { componentItem ->
                                    if (componentItem != null) {
                                        DetailListItem(item = componentItem)
                                        if (componentItem != state.componentDetails.last()) {
                                            HorizontalDivider()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}