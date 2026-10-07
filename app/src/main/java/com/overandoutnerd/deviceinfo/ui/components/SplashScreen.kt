package com.overandoutnerd.deviceinfo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.overandoutnerd.deviceinfo.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    show: Boolean,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = show,
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.device_info_logo_wide),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp, 40.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                LinearProgressIndicator(
                    modifier = Modifier.padding(horizontal = 64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading Device Information...",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (show) {
        LaunchedEffect(Unit) {
            // Simulate initial loading time or wait for actual data
            delay(2000)
            onDismiss()
        }
    }
}
