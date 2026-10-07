package com.overandoutnerd.deviceinfo.appsanalyze.minimumapi

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.ads.NativeAdItem
import com.overandoutnerd.deviceinfo.ads.NativeAdView
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeComponent
import com.overandoutnerd.deviceinfo.appsanalyze.logic.ApiLevel
import com.overandoutnerd.deviceinfo.appsanalyze.logic.SdkType
import com.overandoutnerd.deviceinfo.appsanalyze.logic.apiToComposeColor
import com.overandoutnerd.deviceinfo.home.ContextViewModelFactory
import com.overandoutnerd.deviceinfo.ui.StorageListItem
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.overandoutnerd.deviceinfo.ui.components.SdkBarChart
import com.overandoutnerd.deviceinfo.ui.theme.BottomBarContentClearance
import com.farimarwat.composenativeadmob.nativead.rememberNativeAdState
import com.kyant.backdrop.Backdrop

@Composable
fun MinimumApiScreen(
    context: Context,
    backdrop: Backdrop,
    onChipClick: (AnalyzeComponent<Int>) -> Unit,
    onRetry: () -> Unit
) {
    val viewModel: MinimumApiViewModel = viewModel(factory = ContextViewModelFactory(context))
    val viewState by viewModel.minApiDetailsState
    val appsState by viewModel.appsState

    val showAds = LocalResources.current.getBoolean(R.bool.show_ads)
    val adState = if (showAds) {
        rememberNativeAdState(
            context = context,
            adUnitId = "ca-app-pub-3940256099942544/2247696110"
        )
    } else null

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when {
            viewState.loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            viewState.error != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.analyze_tab_load_error_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = viewState.error ?: stringResource(id = R.string.analyze_tab_load_error_subtitle),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                    Button(onClick = onRetry) {
                        Text(text = stringResource(id = R.string.analyze_tab_load_error_retry))
                    }
                }
            }
            else -> {
                val items = viewState.componentItems ?: emptyList()
                LazyColumn(
                    contentPadding = PaddingValues(bottom = BottomBarContentClearance)
                ) {
                    item {
                        RoundedCornerBox(
                            outerPadding = PaddingValues(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 6.dp)
                        ) {
                            SdkBarChart(
                                data = items,
                                sdkType = SdkType.SDK_MINIMUM,
                                modifier = Modifier.height(200.dp)
                            )
                        }
                    }

                    if (adState != null) {
                        item {
                            RoundedCornerBox(
                                outerPadding = PaddingValues(12.dp, 6.dp),
                                innerPadding = PaddingValues(12.dp)
                            ) {
                                NativeAdView(ad = adState) { ad, view ->
                                    NativeAdItem(modifier = Modifier.padding(5.dp), loadedAd = ad, composeView = view)
                                }
                            }
                        }


                    }

                    item {
                        if (viewState.componentItems.isNotEmpty()) {
                            RoundedCornerBox(
                                outerPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                innerPadding = PaddingValues(12.dp)
                            ) {
                                Column {
                                    viewState.componentItems.forEachIndexed { index, sdk ->
                                        StorageListItem(
                                            title = ApiLevel.getAndroidVersionName(sdk.id, context),
                                            subtitle = "API ${sdk.id}",
                                            value = "${sdk.appCount}",
                                            progress = sdk.percentage,
                                            chipColor = (sdk.id.apiToComposeColor(SdkType.SDK_MINIMUM)).copy(0.1f),
                                            progressColor = sdk.id.apiToComposeColor(SdkType.SDK_MINIMUM),
                                            onChipClick = { onChipClick(sdk)  }
                                        )
                                        if (index < viewState.componentItems.size - 1) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(vertical = 4.dp),
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


}