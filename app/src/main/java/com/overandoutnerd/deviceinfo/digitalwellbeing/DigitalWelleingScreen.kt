package com.overandoutnerd.deviceinfo.digitalwellbeing

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.overandoutnerd.deviceinfo.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.overandoutnerd.deviceinfo.home.ContextViewModelFactory
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.overandoutnerd.deviceinfo.ui.theme.BottomBarContentClearance

@Composable
fun WellbeingScreen(
    context: Context,
    hasUsageStatsPermission: (Boolean) -> Boolean
){
    val viewModel: ScreenUsageViewModel = viewModel(factory = ContextViewModelFactory(context))
    val hasUsagePermission by remember {
        mutableStateOf(hasUsageStatsPermission(false))
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (hasUsagePermission)
        {
            val viewState by viewModel.screenUsageState
            when {
                viewState.loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                viewState.error != null -> Text(text = "Error Occurred ${viewState.error}")
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = BottomBarContentClearance)
                    ) {
                        item {
                            RoundedCornerBox(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = viewState.screenUsage.dayDuration,
                                    style = MaterialTheme.typography.displayMedium,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        item {
                            RoundedCornerBox(modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    viewState.screenUsage.usedApps.forEach {
                                        UsedApp(usedApp = it)
                                    }
                                }

                            }
                        }
                    }
                }
            }
        } else {
            val usageAccessImage = painterResource(id = viewModel.usagePermissionImage.value)
            UsagePermissionScreen(
                image = usageAccessImage,
                title = stringResource(id = R.string.usage_permission_screen_title),
                description = stringResource(id = R.string.usage_permission_screen_description),
                privacyInfo = stringResource(id = R.string.usage_permission_screen_privacy_info),
                buttonTitle = stringResource(id = R.string.usage_permission_screen_button_title),
                onButtonClick =  {
                    hasUsageStatsPermission(true)
                }
            )
        }
    }
}

@Composable
fun UsagePermissionScreen(
    image: Painter,
    title: String,
    description: String,
    privacyInfo: String,
    buttonTitle: String,
    onButtonClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = BottomBarContentClearance)
    ) {
        item {
            Image(
                painter = image,
                contentDescription = title,
                modifier = Modifier.padding(15.dp, 0.dp).fillMaxWidth()
            )
        }
        item {
            RoundedCornerBox {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(0.dp, 8.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = privacyInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(0.dp, 8.dp),
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = { onButtonClick() }) {
                        Text(text = buttonTitle)
                    }
                }
            }
        }

    }
}

@Composable
fun UsedApp(
    usedApp: AppUsage,
    modifier: Modifier = Modifier
){
    val logo = usedApp.app.icon?.toBitmap(width = 90, height = 90)?.asImageBitmap()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 2.dp)
            .then(modifier)
            .clip(RoundedCornerShape(26.dp))
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (logo != null) {
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        RoundedCornerShape(27)
                    )
                    .clip(RoundedCornerShape(27)),
                bitmap = logo,
                contentDescription = null
            )
        }
        Column {
            Text(
                text = usedApp.app.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = usedApp.appDuration,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

    }

}