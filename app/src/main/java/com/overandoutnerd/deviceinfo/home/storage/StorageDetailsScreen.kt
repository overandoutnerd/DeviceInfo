package com.overandoutnerd.deviceinfo.home.storage

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.ads.NativeAdItem
import com.overandoutnerd.deviceinfo.ads.NativeAdView
import com.overandoutnerd.deviceinfo.home.ContextViewModelFactory
import com.overandoutnerd.deviceinfo.ui.StorageListItem
import com.overandoutnerd.deviceinfo.ui.components.MemoryCard
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.overandoutnerd.deviceinfo.ui.theme.BottomBarContentClearance
import com.farimarwat.composenativeadmob.nativead.rememberNativeAdState

@Composable
fun StorageDetailsScreen(
    context: Context
) {
    val viewModel: StorageDetailsViewModel = viewModel(factory = ContextViewModelFactory(context))
    val viewState by viewModel.storageDetailsState

    val showAds = LocalResources.current.getBoolean(R.bool.show_ads)
    val adState = if (showAds) {
        rememberNativeAdState(
            context = context,
            adUnitId = "ca-app-pub-3940256099942544/2247696110"
        )
    } else null

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            viewState.loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            viewState.error != null -> {
                Text(text = "${viewState.error}")
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = BottomBarContentClearance)
                ) {
                    if (adState != null) {
                        item {
                            RoundedCornerBox(
                                innerPadding = PaddingValues(12.dp),
                                outerPadding = PaddingValues(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 6.dp)
                            ) {
                                NativeAdView(ad = adState) { ad, view ->
                                    NativeAdItem(modifier = Modifier.padding(5.dp), loadedAd = ad, composeView = view)
                                }
                            }
                        }
                    }

                    if (viewState.memoryInfoList.isNotEmpty()) {
                        items(viewState.memoryInfoList) { memoryInfo ->
                            MemoryCard(memoryInfo = memoryInfo)
                        }
                    } else {
                        item {
                            RoundedCornerBox(
                                outerPadding = PaddingValues(horizontal = 12.dp, vertical = if (adState != null) 6.dp else 12.dp)
                            ) {
                                Column {
                                    viewState.storageDetails.forEach { item ->
                                        if (item != null) {
                                            StorageListItem(
                                                title = item.title,
                                                subtitle = item.subtitle,
                                                value = item.value,
                                                progress = item.progress
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
