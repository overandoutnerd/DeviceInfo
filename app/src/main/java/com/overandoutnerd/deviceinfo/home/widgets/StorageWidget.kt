package com.overandoutnerd.deviceinfo.home.widgets

import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.datastore.preferences.core.Preferences
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.overandoutnerd.deviceinfo.R

class StorageWidget: GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {

            val prefs = currentState<Preferences>()
            val storageInfo = when (prefs[StorageInfo.statusKey]) {
                "Available" -> StorageInfo.Available(
                    total = prefs[StorageInfo.totalKey] ?: "",
                    free = prefs[StorageInfo.freeKey] ?: "",
                    progress = prefs[StorageInfo.progressKey] ?: 0f,
                    value = prefs[StorageInfo.valueKey] ?: ""
                )

                "Unavailable" -> StorageInfo.Unavailable(
                    message = prefs[StorageInfo.messageKey] ?: ""
                )

                else -> StorageInfo.Loading
            }

            GlanceTheme {
                when(storageInfo) {
                    StorageInfo.Loading -> {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is StorageInfo.Available -> {
                        StorageWidgetCard(storageInfo = storageInfo)
                    }
                    is StorageInfo.Unavailable -> {
                        Box(contentAlignment = Alignment.Center) {
                            Column {
                                Text("Storage Details not available")
                                Button("Refresh", onClick = actionRunCallback<UpdateStorageAction>())
                            }
                            Text(text = storageInfo.message)
                        }
                    }
                }
            }

        }

    }

}

class UpdateStorageAction: ActionCallback {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        StorageWorker.enqueue(context = context, force = true)
    }
}

@Composable
fun StorageWidgetCard(
    modifier: Modifier = Modifier,
    storageInfo: StorageInfo.Available
) {

    val storageDetails = StorageDetails(
        R.drawable.ic_storage,
        R.string.dashboard_item_storage_title,
        R.string.dashboard_item_storage_subtitle,
        storageInfo
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            provider = ImageProvider(storageDetails.icon),
            contentDescription = "Storage",
            colorFilter = ColorFilter.tint(
                GlanceTheme.colors.primary
            ),
            modifier = GlanceModifier
                .size(48.dp)
                .padding(end = 8.dp)
        )
        Column(
            modifier = GlanceModifier.defaultWeight()
        ){
            Text(
                text = stringResource(storageDetails.title),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GlanceTheme.colors.onSurface
                )
            )
            LinearProgressIndicator(
                modifier = GlanceModifier.padding(0.dp, 5.dp),
                progress = storageDetails.storageInfo.progress/100,
            )
            Text(
                text = stringResource(
                    id = storageDetails.subTitle,
                    storageDetails.storageInfo.free, storageDetails.storageInfo.total
                )
            )
        }
        Text(
            text = storageDetails.storageInfo.value,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = GlanceTheme.colors.onSurface
            ),
            modifier = GlanceModifier.padding(start = 5.dp)
        )
    }
}

data class StorageDetails(
    @DrawableRes val icon: Int = 0,
    @StringRes val title: Int = 0,
    @StringRes val subTitle: Int = 0,
    val storageInfo: StorageInfo.Available = StorageInfo.Available()
)
