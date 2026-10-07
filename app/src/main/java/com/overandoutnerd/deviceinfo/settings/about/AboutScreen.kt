package com.overandoutnerd.deviceinfo.settings.about

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox

@Composable
fun AboutScreen(context: Context) {
    Column() {
        RoundedCornerBox() {
            Column() {
                Image(
                    painter = painterResource(R.drawable.device_info_light_logo_big),
                    ""
                )
                Text(text = context.getString(R.string.app_name))
                Box(){
                    Text(text = context.getString(R.string.app_version))
                }
            }
        }
        RoundedCornerBox() {
            Column() {
                Row() {
                    Box() { }
                }
            }
        }
    }
}