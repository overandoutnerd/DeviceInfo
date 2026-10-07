package com.overandoutnerd.deviceinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.overandoutnerd.deviceinfo.home.dashboard.RamDetails

@Composable
fun RamDetailsCard(
    ramDetails: RamDetails
){
    val percentageTitle = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp
            )
        ) {
            append("${ramDetails.progress.toInt()}")
        }
        append("%")
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier){
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(120.dp)
                    .rotate(-135f),
                progress = { ramDetails.progress/100 },
                color = MaterialTheme.colorScheme.onPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f),
                strokeWidth = 8.dp,
                strokeCap = StrokeCap.Round
            )
            Text(
                text = percentageTitle,
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
        ) {
            Text(
                modifier = Modifier.padding(5.dp),
                text = ramDetails.used,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                modifier = Modifier.padding(5.dp, 0.dp),
                text = ramDetails.available,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = ramDetails.total,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

val sampleRamDetails = RamDetails(
    total = "40000000",
    used = "30000000",
    available = "10000000",
    progress = 50f
)

@Preview
@Composable
fun RamDetailsCardPreview(){
    RamDetailsCard(ramDetails = sampleRamDetails)
}