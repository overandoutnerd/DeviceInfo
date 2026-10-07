package com.overandoutnerd.deviceinfo.ui.components

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomSpeedometer(
    modifier: Modifier = Modifier,
    value: Double,
    subtitle: String
) {
    val progress by remember { mutableStateOf((value/100) * 300) }
    val targetAnimatedValue by remember { mutableStateOf(progress) }
    val progressAnimate by remember {
        mutableStateOf(Animatable(progress.toFloat()))
    }
    Log.e("Speedometer", "Progress Animate: ${progressAnimate.value}")
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(value) {
        progressAnimate.animateTo(
            targetValue = progress.toFloat(),
            animationSpec = tween(1000)
        )
    }

    Column(
//        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val start = 120f
        val end = 300f
        val thickness = 8.dp
        val bgColor = MaterialTheme.colorScheme.secondaryContainer
        val fgColor = MaterialTheme.colorScheme.primary
        Log.e("Speedometer", "Progress Value: $progress")
        Box(modifier = modifier.fillMaxHeight()) {
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(10.dp)
                    .aspectRatio(1f)
                    .align(Alignment.Center),
                onDraw = {
                    // Background Arc
                    drawArc(
                        color = bgColor,
                        startAngle = start,
                        sweepAngle = end,
                        useCenter = false,
                        style = Stroke(thickness.toPx(), cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )

                    // Foreground Arc
                    drawArc(
                        color = fgColor,
                        startAngle = start,
                        sweepAngle = progressAnimate.value,
                        useCenter = false,
                        style = Stroke(thickness.toPx(), cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )
                }
            )
            Column(
                modifier = modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    text = "${value.toInt()}%",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.displaySmall
                )

                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArcProgressPreview(){
    CustomSpeedometer(value = 50.0, subtitle = "Level")
}