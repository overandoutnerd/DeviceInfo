package com.overandoutnerd.deviceinfo.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.overandoutnerd.deviceinfo.home.UserDeviceDetailsProperty

@Composable
fun DetailListItem(item: UserDeviceDetailsProperty, modifier: Modifier = Modifier){
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        clipboardManager.setText(
                            AnnotatedString(
                                "${item.key}: ${item.value}"
                            )
                        )
                    }
                )
            }
            .padding(0.dp, 5.dp)
    ) {
        Text(
            modifier = Modifier.padding(0.dp, 2.dp),
            text = item.key,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier.padding(0.dp, 2.dp),
            text = item.value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}