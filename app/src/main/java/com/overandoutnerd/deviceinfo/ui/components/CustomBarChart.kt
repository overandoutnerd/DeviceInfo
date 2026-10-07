package com.overandoutnerd.deviceinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overandoutnerd.deviceinfo.appsanalyze.logic.apiToComposeColor
import androidx.core.graphics.drawable.toBitmap
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeComponent
import com.overandoutnerd.deviceinfo.appsanalyze.logic.SdkType

@Composable
fun <T> SdkBarChart(
    data: List<AnalyzeComponent<T>>,
    sdkType: SdkType,
    modifier: Modifier = Modifier,
) {
    val maxCount = data.maxOfOrNull { it.appCount }?.coerceAtLeast(1) ?: 1
    BoxWithConstraints(modifier = modifier) {
        val spacing = (-8).dp
        val visibleItems = data.size.coerceIn(1, 6)
        val availableWidth = maxWidth - 20.dp
        val itemWidth = ((availableWidth - spacing * (visibleItems - 1)) / visibleItems).coerceIn(44.dp, 88.dp)

        LazyRow(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            items(data) { item ->
                val logo = remember(item.id) {
                    item.icon.toBitmap(width = 100, height = 100).asImageBitmap()
                }
                val color = item.id.apiToComposeColor(sdkType)
                val fraction = (item.appCount.toFloat() / maxCount).coerceIn(0.06f, 1f)
                Column(
                    modifier =
                        Modifier
                            .width(itemWidth)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(10.dp))
                            .padding(horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .width(itemWidth * 0.62f)
                                    .fillMaxHeight(fraction)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 8.dp,
                                            topEnd = 8.dp,
                                            bottomStart = 4.dp,
                                            bottomEnd = 4.dp,
                                        ),
                                    ).background(
                                        Brush.verticalGradient(
                                            listOf(color, color.copy(alpha = 0.68f)),
                                        ),
                                    ),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            if (fraction >= 0.18f) {
                                Text(
                                    text = item.appCount.toString(),
                                    modifier = Modifier.padding(top = 5.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                    when(item.id){
                        is Int -> {
                            Text(
                                text = item.id.toString(),
                                modifier = Modifier.padding(top = 5.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SdkBarChartPreview() {
//    val sampleData = listOf(
//        SdkDistribution(sdkVersion = 30, appCount = 5, percentage = 0.25f),
//        SdkDistribution(sdkVersion = 31, appCount = 12, percentage = 0.60f),
//        SdkDistribution(sdkVersion = 33, appCount = 20, percentage = 1.0f),
//        SdkDistribution(sdkVersion = 34, appCount = 8, percentage = 0.40f),
//        SdkDistribution(sdkVersion = 35, appCount = 2, percentage = 0.10f)
//    )
//    DeviceInfoAppTheme(darkTheme = false) {
//        SdkBarChart(
//            data = sampleData,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 16.dp),
//
//        )
//    }
//}
