package com.overandoutnerd.deviceinfo.home.sensors

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.home.ContextViewModelFactory
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.overandoutnerd.deviceinfo.ui.components.SensorRow
import com.overandoutnerd.deviceinfo.ui.theme.BottomBarContentClearance

@Composable
fun SensorsScreen(
    context: Context,
    navigateToSensorDetails: (SimpleSensorDetails) -> Unit
) {
    val viewModel: SensorsViewModel = viewModel(factory = ContextViewModelFactory(context))
    val state by viewModel.sensorsState

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            state.error != null -> Text(
                text = stringResource(id = R.string.sensors_screen_error),
                modifier = Modifier.align(Alignment.Center)
            )
            state.sensors.isEmpty() -> Text(
                text = stringResource(id = R.string.sensors_screen_empty),
                modifier = Modifier.align(Alignment.Center)
            )
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = BottomBarContentClearance)
                ) {
                    item {
                        Text(
                            text = stringResource(id = R.string.sensors_screen_count, state.sensors.size),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                    item {
                        RoundedCornerBox {
                            Column {
                                state.sensors.forEachIndexed { index, sensor ->
                                    SensorRow(
                                        icon = sensor.category.icon,
                                        name = sensor.name,
                                        subtitle = sensor.vendor,
                                        onClick = { navigateToSensorDetails(sensor) }
                                    )
                                    if (index < state.sensors.lastIndex) {
                                        HorizontalDivider(
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant
                                        )
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
