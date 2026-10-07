package com.overandoutnerd.deviceinfo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.model.toChartDataSet

@Composable
fun CustomLineChart(
    title: String,
    values: List<Float>,
    labels: List<String>? = null,
    modifier: Modifier = Modifier
) {

    val dataSet = values.toChartDataSet(
        title = title,
        labels = labels,
    )

    LineChart(dataSet)
}

@Preview
@Composable
fun LineChartPreview() {
    CustomLineChart(
        "Ram Usage",
        listOf(42f, 38f, 45f, 51f, 47f, 54f, 49f),
//        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    )
}