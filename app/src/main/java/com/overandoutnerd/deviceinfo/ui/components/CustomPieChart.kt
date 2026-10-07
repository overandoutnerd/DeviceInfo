package com.overandoutnerd.deviceinfo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat

data class Slice(
    val title: String,
    val value: Float,
    val color: Color
)

data class Pies(val slices: List<Slice>) {
    fun totalSize(): Float = slices.map { it.value }.sum()
}

interface SliceDrawer {
    fun drawSlice(
        drawScope: DrawScope,
        canvas: Canvas,
        area: Size,
        startAngle: Float,
        sweepAngle: Float,
        slice: Slice
    )
}

class FilledSliceDrawer(private val thickness: Float = 30f) : SliceDrawer {

    private val sectionPaint = Paint().apply {
        isAntiAlias = true
        style = PaintingStyle.Stroke
    }

    override fun drawSlice(
        drawScope: DrawScope,
        canvas: Canvas,
        area: Size,
        startAngle: Float,
        sweepAngle: Float,
        slice: Slice
    ) {
        val sliceThickness = calculateSectorThickness(area = area)
        val drawableArea = calculateDrawableArea(area = area)

        canvas.drawArc(
            rect = drawableArea,
            paint = sectionPaint.apply {
                color = slice.color
                strokeWidth = sliceThickness
            },
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false
        )
    }

    private fun calculateSectorThickness(area: Size): Float {
        val minSize = minOf(area.width, area.height)

        return minSize * (thickness / 200f)
    }

    private fun calculateDrawableArea(area: Size): Rect {
        val sliceThicknessOffset =
            calculateSectorThickness(area = area) / 2f
        val offsetHorizontally = (area.width - area.height) / 2f

        return Rect(
            left = sliceThicknessOffset + offsetHorizontally,
            top = sliceThicknessOffset,
            right = area.width - sliceThicknessOffset - offsetHorizontally,
            bottom = area.height - sliceThicknessOffset
        )
    }
}

@Composable
fun CustomPieChart(
    pies: Pies,
    modifier: Modifier = Modifier,
    sliceDrawer: SliceDrawer = FilledSliceDrawer()
) {
    val transitionProgress = remember(pies.slices) { Animatable(initialValue = 0f) }

    LaunchedEffect(pies.slices) {
        transitionProgress.animateTo(1f)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        DrawChart(
            pies = pies,
            modifier = modifier.fillMaxSize().weight(1f),
            progress = transitionProgress.value,
            sliceDrawer = sliceDrawer
        )
        Column(
            modifier = Modifier
                .wrapContentSize()
        ) {
            val slices = pies.slices
            slices.forEach {
                DetailsCustomPieChartItem(data = Pair(it.title, it.value.toDouble()), color = it.color)
            }
        }
    }


}

@Composable
private fun DrawChart(
    pies: Pies,
    modifier: Modifier,
    progress: Float,
    sliceDrawer: SliceDrawer
) {
    val slices = pies.slices

    Canvas(modifier = modifier) {
        drawIntoCanvas {
            var startArc = 0f

            slices.forEach { slice ->
                val arc = angleOf(
                    value = slice.value,
                    totalValue = pies.totalSize(),
                    progress = progress
                )

                sliceDrawer.drawSlice(
                    drawScope = this,
                    canvas = drawContext.canvas,
                    area = size,
                    startAngle = startArc,
                    sweepAngle = arc,
                    slice = slice
                )

                startArc += arc
            }
        }
    }
}

@Composable
fun DetailsCustomPieChartItem(
    data: Pair<String, Double>,
    height: Dp = 8.dp,
    color: Color
) {
    val percentage = DecimalFormat("#.#").format(data.second)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(0.dp, 1.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = color,
                    shape = RoundedCornerShape(1.dp)
                )
                .size(height)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "${percentage}% - ${data.first}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium
        )
    }

}

fun angleOf(value: Float, totalValue: Float, progress: Float): Float {
    return 360f * (value * progress) / totalValue
}