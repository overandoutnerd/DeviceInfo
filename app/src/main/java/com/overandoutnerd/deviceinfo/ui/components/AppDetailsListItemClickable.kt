package com.overandoutnerd.deviceinfo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.overandoutnerd.deviceinfo.home.apps.details.AppDetails
import com.overandoutnerd.deviceinfo.home.apps.details.AppDetailsComponent

@Composable
fun AppDetailsListItemClickable(
    component: AppDetailsComponent,
    packageName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .background(color = MaterialTheme.colorScheme.background)
            .clickable { component.action(component.title, packageName, component.flag) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(
                text = component.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(2.dp)
            )
            Text(
                text = "Total: ${component.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(2.dp)
            )
        }

        Image(
            modifier = Modifier
                .padding(8.dp),
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null
        )
    }
}