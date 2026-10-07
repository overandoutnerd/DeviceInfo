package com.overandoutnerd.deviceinfo.home.sensors

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.home.UserDeviceDetailsProperty
import com.overandoutnerd.deviceinfo.ui.components.DetailListItem
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.overandoutnerd.deviceinfo.ui.theme.BottomBarContentClearance
import com.overandoutnerd.deviceinfo.ui.theme.healthAccentBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorDetailsScreen(
    sensor: SimpleSensorDetails?,
    onBackPressed: () -> Unit
) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.sensor_details_app_bar_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
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
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .padding(padding)
        ) {
            if (sensor == null) {
                Text(
                    text = stringResource(id = R.string.sensor_details_not_found),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = BottomBarContentClearance)
                ) {
                    item {
                        SensorDetailsHeader(sensor)
                    }
                    item {
                        RoundedCornerBox {
                            Column {
                                val properties = sensorProperties(sensor)
                                properties.forEachIndexed { index, property ->
                                    DetailListItem(item = property)
                                    if (index < properties.lastIndex) {
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

@Composable
private fun SensorDetailsHeader(sensor: SimpleSensorDetails) {
    RoundedCornerBox {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color = healthAccentBlue.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = sensor.category.icon,
                    contentDescription = null,
                    tint = healthAccentBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(
                    text = sensor.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sensor.vendor,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun sensorProperties(sensor: SimpleSensorDetails): List<UserDeviceDetailsProperty> {
    val microsToMillis = { micros: Int -> "%.2f ms".format(micros / 1000f) }
    return listOfNotNull(
        UserDeviceDetailsProperty(stringResource(id = R.string.sensor_details_item_type), sensor.type),
        UserDeviceDetailsProperty(stringResource(id = R.string.sensor_details_item_vendor), sensor.vendor),
        UserDeviceDetailsProperty(stringResource(id = R.string.sensor_details_item_version), sensor.version.toString()),
        UserDeviceDetailsProperty(
            stringResource(id = R.string.sensor_details_item_power),
            stringResource(id = R.string.sensor_details_value_power, sensor.power)
        ),
        UserDeviceDetailsProperty(
            stringResource(id = R.string.sensor_details_item_max_range),
            sensor.maxRange.toString()
        ),
        UserDeviceDetailsProperty(
            stringResource(id = R.string.sensor_details_item_resolution),
            sensor.resolution.toString()
        ),
        UserDeviceDetailsProperty(
            stringResource(id = R.string.sensor_details_item_min_delay),
            microsToMillis(sensor.minDelayMicros)
        ),
        if (sensor.maxDelayMicros > 0) {
            UserDeviceDetailsProperty(
                stringResource(id = R.string.sensor_details_item_max_delay),
                microsToMillis(sensor.maxDelayMicros)
            )
        } else null,
        UserDeviceDetailsProperty(
            stringResource(id = R.string.sensor_details_item_reporting_mode),
            reportingModeLabel(sensor.reportingMode)
        ),
        UserDeviceDetailsProperty(
            stringResource(id = R.string.sensor_details_item_wake_up),
            stringResource(id = if (sensor.isWakeUpSensor) R.string.sensor_details_value_yes else R.string.sensor_details_value_no)
        ),
        UserDeviceDetailsProperty(
            stringResource(id = R.string.sensor_details_item_dynamic),
            stringResource(id = if (sensor.isDynamicSensor) R.string.sensor_details_value_yes else R.string.sensor_details_value_no)
        ),
        if (sensor.id != 0) {
            UserDeviceDetailsProperty(stringResource(id = R.string.sensor_details_item_id), sensor.id.toString())
        } else null
    )
}
