package com.overandoutnerd.deviceinfo.ads

import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import com.overandoutnerd.deviceinfo.R
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import androidx.compose.ui.platform.LocalResources

@Composable
fun NativeAdItem(
    modifier: Modifier,
    loadedAd: NativeAd?,
    composeView: View
) {
    if (loadedAd != null && LocalResources.current.getBoolean(R.bool.show_ads)) {
        val adLogo = loadedAd.icon?.drawable?.toBitmap(width = 80, height = 80)?.asImageBitmap()
        val adChoiceIcon = loadedAd.adChoicesInfo?.images?.get(0)?.drawable?.toBitmap(width = 5, height = 5)?.asImageBitmap()
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (adLogo != null) {
                    Image(
                        modifier = Modifier
                            .padding(top = 2.dp, end = 8.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(8.dp)
                            )
                            .clip(RoundedCornerShape(8.dp)),
                        bitmap = adLogo,
                        contentDescription = null
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.padding(bottom = 1.dp),
                        text = "${loadedAd.headline}",
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(modifier = Modifier.padding(top = 2.dp)) {
                        Text(
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .background(
                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                    RoundedCornerShape(5.dp)
                                )
                                .padding(5.dp, 1.dp),
                            text = stringResource(R.string.native_ad_attribute_label),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                        if (adChoiceIcon != null) {
                            Image(
                                bitmap = adChoiceIcon,
                                contentDescription = null
                            )
                        }
                        if (loadedAd.advertiser != null) {
                            Text(
                                text = "${loadedAd.advertiser}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                AssistChip(
                    onClick = { /*TODO*/ },
                    label = {
                        loadedAd.callToAction?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    }
                )
            }
            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = "${loadedAd.body}",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DashboardNativeAdItem(loadedAd: NativeAd?, composeView: View) {
    if (loadedAd != null) {
        val adLogo = loadedAd.icon?.drawable?.toBitmap(width = 80, height = 80)?.asImageBitmap()
        val adChoiceIcon = loadedAd.adChoicesInfo?.images?.get(0)?.drawable?.toBitmap(width = 5, height = 5)?.asImageBitmap()
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (adLogo != null) {
                    Image(
                        modifier = Modifier
                            .padding(top = 2.dp, end = 8.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(8.dp)
                            )
                            .clip(RoundedCornerShape(8.dp)),
                        bitmap = adLogo,
                        contentDescription = null
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.padding(bottom = 1.dp),
                        text = "${loadedAd.headline}",
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(modifier = Modifier.padding(top = 2.dp)) {
                        Text(
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .background(
                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                    RoundedCornerShape(5.dp)
                                )
                                .padding(5.dp, 1.dp),
                            text = stringResource(R.string.native_ad_attribute_label),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                        if (adChoiceIcon != null) {
                            Image(
                                bitmap = adChoiceIcon,
                                contentDescription = null
                            )
                        }
                        if (loadedAd.advertiser != null) {
                            Text(
                                text = "${loadedAd.advertiser}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Button(
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(12.dp, 4.dp),
                    onClick = {  }
                ) {
                    loadedAd.callToAction?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = "${loadedAd.body}",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MediumNativeAdItem(
    modifier: Modifier,
    loadedAd: NativeAd?,
    composeView: View
) {
    if (loadedAd != null) {
        val adLogo = loadedAd.icon?.drawable?.toBitmap(width = 125, height = 125)?.asImageBitmap()
        val adChoiceIcon = loadedAd.adChoicesInfo?.images?.get(0)?.drawable?.toBitmap(width = 5, height = 5)?.asImageBitmap()
        Column {
            Row {
                if (adLogo != null) {
                    Image(
                        modifier = Modifier
                            .padding(top = 2.dp, end = 8.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp)),
                        bitmap = adLogo,
                        contentDescription = null
                    )
                }
                Column {
                    Text(
                        modifier = Modifier.padding(bottom = 1.dp),
                        text = "${loadedAd.headline}",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${loadedAd.body}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(modifier = Modifier.padding(top = 5.dp)) {
                        Text(
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .background(
                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                    RoundedCornerShape(5.dp)
                                )
                                .padding(5.dp, 1.dp),
                            text = stringResource(R.string.native_ad_attribute_label),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                        if (adChoiceIcon != null) {
                            Image(
                                bitmap = adChoiceIcon,
                                contentDescription = null
                            )
                        }
                        if (loadedAd.advertiser != null) {
                            Text(
                                text = "${loadedAd.advertiser}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            if (loadedAd.callToAction != null) {
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    onClick = {}
                ) {
                    Text(
                        text = "${loadedAd.callToAction}"
                    )
                }
            }
        }
    }
}

@Composable
fun LargeNativeAdItem(
    modifier: Modifier,
    loadedAd: NativeAd?,
    composeView: View
){
    if (loadedAd != null) {
        val adLogo = loadedAd.icon?.drawable?.toBitmap(width = 120, height = 120)?.asImageBitmap()
        val adBanner = loadedAd.images[0].drawable?.toBitmap(width = 1280, height = 720)?.asImageBitmap()
        val adChoiceIcon = loadedAd.adChoicesInfo?.images?.get(0)?.drawable?.toBitmap(width = 5, height = 5)?.asImageBitmap()
        Box {
            Column {
                Row() {
                    if (adLogo != null) {
                        Image(
                            modifier = Modifier
                                .padding(top = 2.dp, end = 8.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    RoundedCornerShape(12.dp)
                                )
                                .clip(RoundedCornerShape(12.dp)),
                            bitmap = adLogo,
                            contentDescription = null
                        )
                    }
                    Column {
                        Text(
                            modifier = Modifier.padding(bottom = 1.dp),
                            text = "${loadedAd.headline}",
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${loadedAd.body}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(modifier = Modifier.padding(top = 5.dp)) {
                            Text(
                                modifier = Modifier
                                    .padding(end = 2.dp)
                                    .background(
                                        MaterialTheme.colorScheme.onPrimaryContainer,
                                        RoundedCornerShape(5.dp)
                                    )
                                    .padding(5.dp, 1.dp),
                                text = stringResource(R.string.native_ad_attribute_label),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                            if (adChoiceIcon != null) {
                                Image(
                                    bitmap = adChoiceIcon,
                                    contentDescription = null
                                )
                            }
                            if (loadedAd.advertiser != null) {
                                Text(
                                    text = "${loadedAd.advertiser}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                        }
                    }
                }
                if (adBanner != null) {
                    Image(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp)),
                        bitmap = adBanner,
                        contentDescription = null
                    )
                }
                if (loadedAd.callToAction != null) {
                    Button(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        onClick = {}
                    ) {
                        Text(
                            text = "${loadedAd.callToAction}"
                        )
                    }
                }
            }
            if (adChoiceIcon != null) {
                Image(
                    modifier = Modifier.align(Alignment.TopEnd),
                    bitmap = adChoiceIcon,
                    contentDescription = null
                )
            }
        }
    }
}


@Composable
fun NativeAdView(
    ad: NativeAd?,
    adContent: @Composable (ad: NativeAd, contentView: View) -> Unit
) {
    if (ad != null) {
        val contentViewId by remember { mutableIntStateOf(View.generateViewId()) }
        val adViewId by remember { mutableIntStateOf(View.generateViewId()) }
        AndroidView(
            factory = { context ->
                val contentView = ComposeView(context).apply {
                    id = contentViewId
                }
                NativeAdView(context).apply {
                    id = adViewId
                    addView(contentView)
                }
            },
            update = { view ->
                val adView = view.findViewById<NativeAdView>(adViewId)
                val contentView = view.findViewById<ComposeView>(contentViewId)

                adView.setNativeAd(ad)
                adView.callToActionView = contentView
                contentView.setContent { adContent(ad, contentView) }
            }
        )
    }

}