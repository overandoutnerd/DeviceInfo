package com.overandoutnerd.deviceinfo.home.dashboard

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalResources
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.ads.DashboardNativeAdItem
import com.overandoutnerd.deviceinfo.ads.NativeAdView
import com.overandoutnerd.deviceinfo.home.ContextViewModelFactory
import com.overandoutnerd.deviceinfo.ui.components.RamDetailsCard
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBoxColors
import com.overandoutnerd.deviceinfo.ui.theme.healthAccentBlue
import com.overandoutnerd.deviceinfo.ui.theme.healthAccentGreen
import com.overandoutnerd.deviceinfo.ui.theme.healthAccentPurple
import com.overandoutnerd.deviceinfo.ui.theme.BottomBarContentClearance
import com.farimarwat.composenativeadmob.nativead.rememberNativeAdState

@Composable
fun DashboardScreen(
    context: Context,
    onClickItem: (Int) -> Unit
) {

    val showAds = LocalResources.current.getBoolean(R.bool.show_ads)
    val adState = if (showAds) {
        rememberNativeAdState(
            context = context,
            adUnitId = "ca-app-pub-3940256099942544/2247696110",
            refreshInterval = 0
        )
    } else null

    val viewModel: DashboardViewModel = viewModel(factory = ContextViewModelFactory(context))
    val ramDetailsState by viewModel.ramDetailsState
    val processorDetailsState by viewModel.processorDetailsState
    val appsCountState by viewModel.appsCountState
    val sensorsCountState by viewModel.sensorCountState
    val displayDetailsState by viewModel.displayDetailsState
    val storageDetailsState by viewModel.storageDetailsState
    val batteryDetailsState by viewModel.batteryDetailsLiveData.observeAsState(DashboardViewModel.DashboardProgressItemState())

    LazyColumn(
        contentPadding = PaddingValues(bottom = BottomBarContentClearance)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                when {
                    ramDetailsState.loading -> {
                        RoundedCornerBox(modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    ramDetailsState.error != null -> Text(text = "${ramDetailsState.error}")
                    else -> {
                        RoundedCornerBox(
                            modifier = Modifier.fillMaxWidth(),
                            colors = RoundedCornerBoxColors(background = MaterialTheme.colorScheme.primary)
                        ) {
                            RamDetailsCard(ramDetails = ramDetailsState.ramDetails)
                        }
                    }
                }
            }
        }

        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                when {
                    processorDetailsState.loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    processorDetailsState.error != null -> Text(text = "${processorDetailsState.error}")
                    else -> {
                        ProcessorDetailsCard(processorDetails = processorDetailsState.processorDetails)
                    }
                }
            }
        }
        if (adState != null) {
            item {
                RoundedCornerBox(
                    shape = RoundedCornerShape(12.dp),
                    outerPadding = PaddingValues(12.dp, 6.dp)
                ) {
                    NativeAdView(ad = adState) { ad, view ->
                        DashboardNativeAdItem(loadedAd = ad, composeView = view)
                    }
                }
            }
        }
        item {
            RoundedCornerBox(
                shape = RoundedCornerShape(12.dp),
                outerPadding = PaddingValues(12.dp, 6.dp)
            ) {
                when {
                    storageDetailsState.loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    storageDetailsState.error != null -> Text(text = "${storageDetailsState.error}")
                    else -> {
                        StorageDetailsCard(
                            storageDetailsState.item
                        )
                    }
                }
            }
        }
        item {
            RoundedCornerBox(
                shape = RoundedCornerShape(12.dp),
                outerPadding = PaddingValues(12.dp, 6.dp)
            ) {
                when {
                    batteryDetailsState.loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    batteryDetailsState.error != null -> Text(text = "${batteryDetailsState.error}")
                    else -> {
                        BatteryDetailsCard(
                            batteryDetailsState.item
                        )
                    }
                }
            }
        }
        item {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)) {
               RoundedCornerBox(
                   outerPadding = PaddingValues(6.dp, 0.dp),
                   shape = RoundedCornerShape(12.dp),
                   modifier = Modifier
                       .clickable { onClickItem(1) }
                       .weight(1f)
                       .fillMaxHeight()
               ) {
                   when {
                       appsCountState.loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                       appsCountState.error != null -> Text(text = "${appsCountState.error}")
                       else -> {
                           AppsCountCard(
                               item = appsCountState.item,
                               badgeColor = healthAccentBlue
                           )
                       }
                   }
               }
                RoundedCornerBox(
                    outerPadding = PaddingValues(6.dp, 0.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    when {
                        sensorsCountState.loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        sensorsCountState.error != null -> Text(text = "${sensorsCountState.error}")
                        else -> {
                            AppsCountCard(
                                item = sensorsCountState.item,
                                badgeColor = healthAccentPurple
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ProcessorDetailsCard(processorDetails: ProcessorDetails) {
    val chunkedItems = processorDetails.cores.chunked(3)
    Column {
        Text(
            modifier = Modifier.padding(start = 12.dp, bottom = 4.dp),
            text = processorDetails.title,
            style = MaterialTheme.typography.titleMedium
        )
        chunkedItems.forEach { rowItems ->
            Row(modifier = Modifier.padding(6.dp, 0.dp)) {
                rowItems.forEach { core ->
                    RoundedCornerBox(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        innerPadding = PaddingValues(7.dp),
                        outerPadding = PaddingValues(6.dp)
                    ) {
                        CPUCoreItem(core = core)
                    }
                }
            }
        }
    }
}

@Composable
fun CPUCoreItem(core: CPUCore) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier.padding(bottom = 2.dp),
            text = core.name,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = core.frequency,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun AppsCountCard(
    item: DashboardCountItem,
    badgeColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color = badgeColor.copy(alpha = 0.15f), shape = CircleShape)
                .padding(end = 0.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = "",
                colorFilter = ColorFilter.tint(badgeColor),
                modifier = Modifier.size(24.dp)
            )
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                modifier = Modifier.padding(bottom = 2.dp),
                text = item.value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = item.title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

}

@Composable
fun DisplayDetailsCard(displayDetails: DisplayDetails) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = displayDetails.title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = displayDetails.resolution,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = "${displayDetails.size} | ${displayDetails.refreshRate}",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun StorageDetailsCard(
    item: DashboardProgressItem
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color = healthAccentGreen.copy(alpha = 0.15f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = "",
                colorFilter = ColorFilter.tint(healthAccentGreen),
                modifier = Modifier.size(24.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = stringResource(id = item.title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(0.dp, 6.dp)
                    .fillMaxWidth(),
                progress = { item.storageDetails.progress/100 },
                color = healthAccentGreen,
                trackColor = healthAccentGreen.copy(alpha = 0.15f),
                strokeCap = StrokeCap.Round
            )
            Text(
                text = stringResource(id = item.subTitle, item.storageDetails.free, item.storageDetails.total),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = item.storageDetails.value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 5.dp)
        )
    }
}

@Composable
fun BatteryDetailsCard(
    item: DashboardProgressItem
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color = healthAccentBlue.copy(alpha = 0.15f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = "",
                colorFilter = ColorFilter.tint(healthAccentBlue),
                modifier = Modifier.size(24.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = stringResource(id = item.title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(0.dp, 6.dp)
                    .fillMaxWidth(),
                progress = { item.batteryDetails.progress / 100f },
                color = healthAccentBlue,
                trackColor = healthAccentBlue.copy(alpha = 0.15f),
                strokeCap = StrokeCap.Round
            )
            Text(
                text = stringResource(id = item.subTitle, item.batteryDetails.voltage, item.batteryDetails.temperature),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = item.batteryDetails.value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 5.dp)
        )
    }
}
