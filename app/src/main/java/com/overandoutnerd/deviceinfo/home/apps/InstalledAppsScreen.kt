package com.overandoutnerd.deviceinfo.home.apps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.overandoutnerd.deviceinfo.ui.InstalledApp
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox

@Composable
fun InstalledAppsScreen(installedApps: List<SimpleAppDetails>, navigateToAppDetails: (SimpleAppDetails) -> Unit){
    Column(modifier = Modifier.fillMaxSize()) {
        installedApps.forEach { item ->
            InstalledApp(installedApp = item, navigateToAppDetails = navigateToAppDetails)
        }
    }
}

