package com.overandoutnerd.deviceinfo.home.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.overandoutnerd.deviceinfo.home.ComponentDetailsState
import com.overandoutnerd.deviceinfo.home.ContextViewModelFactory
import com.overandoutnerd.deviceinfo.ui.components.DetailListItem
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.overandoutnerd.deviceinfo.ui.theme.BottomBarContentClearance

@Composable
fun BatteryDetailsScreen(context: Context) {

    val localContext = LocalContext.current
    val viewModel: BatteryDetailsViewModel = viewModel(factory = ContextViewModelFactory(context))
    val batteryReceiver = remember { BatteryDetailsRepository(localContext, viewModel) }

    DisposableEffect(localContext) {
        Log.e("BatteryDetailsScreen", "Registering battery receiver")
        localContext.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        onDispose {
            Log.e("BatteryDetailsScreen", "Unregistering battery receiver")
            localContext.unregisterReceiver(batteryReceiver)
        }
    }

    val state by viewModel.batteryDetailsState

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
