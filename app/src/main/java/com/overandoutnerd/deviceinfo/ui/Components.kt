package com.overandoutnerd.deviceinfo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.home.apps.SimpleAppDetails
import com.overandoutnerd.deviceinfo.home.apps.details.appcomponents.SimpleComponent
import java.text.DecimalFormat

@Composable
fun StorageListItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    value: String,
    progress: Float,
    chipColor: Color? = null,
    progressColor: Color? = null,
    onChipClick: (() -> Unit)? = null
) {
    val chipContainerColor = chipColor ?: AssistChipDefaults.assistChipColors().containerColor
    val progressIndicatorColor = progressColor ?: ProgressIndicatorDefaults.linearColor
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 5.dp)
    ){
        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight()
                .padding(end = 5.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier.padding(2.dp),
                text = title,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null){
                Text(
                    modifier = Modifier.padding(2.dp),
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LinearProgressIndicator(
                progress = { progress/100 },
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .padding(2.dp, 8.dp),
                color = progressIndicatorColor
            )
        }
        AssistChip(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .padding(start = 5.dp)
                .align(Alignment.CenterVertically),
            enabled = true,
            onClick = { onChipClick?.invoke() },
            label = {
                Text(
                    text = value,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            },
            colors = AssistChipDefaults.assistChipColors(containerColor = chipContainerColor)
        )

    }

}

@Composable
fun InstalledApp(
    installedApp: SimpleAppDetails,
    navigateToAppDetails: (SimpleAppDetails) -> Unit,
    modifier: Modifier = Modifier
){
    val logo = remember(installedApp.packageName) {
        installedApp.icon.toBitmap(width = 100, height = 100).asImageBitmap()
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 2.dp)
            .then(modifier)
            .clip(RoundedCornerShape(26.dp))
            .clickable { navigateToAppDetails(installedApp) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(8.dp)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(27)
                )
                .clip(RoundedCornerShape(27)),
            bitmap = logo,
            contentDescription = null
        )
        Text(
            text = installedApp.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}

@Composable
fun AppComponentListItem(
    permission: SimpleComponent,
    modifier: Modifier = Modifier
){
    val showDialog = remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val logo: ImageBitmap = remember(permission.label) {
        if (permission.icon != null) {
            permission.icon.toBitmap(width = 50, height = 50).asImageBitmap()
        } else {
            context.getDrawable(R.drawable.ic_approval)!!.toBitmap(width = 50, height = 50).asImageBitmap()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 2.dp)
            .then(modifier)
            .background(color = MaterialTheme.colorScheme.background)
            .clickable { showDialog.value = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(8.dp)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(27)
                )
                .clip(RoundedCornerShape(27))
                .padding(8.dp),
            bitmap = logo,
            contentDescription = null
        )

        Column {
            Text(
                text = permission.label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 3.dp, bottom = 1.dp)
            )
            Text(
                text = permission.componentPackage,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 1.dp, bottom = 3.dp)
            )
        }

    }

    if (showDialog.value) {
        CustomAlertDialog(
            onDismissRequest = { showDialog.value = false },
            onConfirmation = { showDialog.value = false },
            dialogTitle = permission.label,
            dialogText = permission.description,
            icon = Icons.Default.Info
        )
    }

}

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector
) {
    AlertDialog(
        icon = {
            Icon(imageVector = icon, contentDescription = "")
        },
        title = {
            Text(text = dialogTitle)
        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text(text = "Ok")
            }
        },
        text = {
            Text(text = dialogText)
        }
    )
}


@Composable
fun CustomPieChart(
    data: Map<String, Int>,
    total: Long,
    radiusOuter: Dp = 60.dp,
    chartBarWidth: Dp = 35.dp,
    animDuration: Int = 1000,
) {

    val totalSum = data.values.sum()
    val floatValue = mutableListOf<Float>()

    // To set the value of each Arc according to
    // the value given in the data, we have used a simple formula.
    // For a detailed explanation check out the Medium Article.
    // The link is in the about section and readme file of this GitHub Repository
    data.values.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.toFloat() / totalSum.toFloat())
    }

    // add the colors as per the number of data(no. of pie chart entries)
    // so that each data will get a color
    val colors = listOf(
        Color(0xFFC12652),
        Color(0xFF4CBB17),
        Color(0xFFFFA500),
        Color(0xFFFF5733),
        Color(0xFF4169E1),
        Color(0xFF00FFFF),
        Color(0xFFFF00FF),
        Color(0xFFA52A2A),
        Color(0xFFFFC0CB),
        Color(0xFF008080),
        Color(0xFF808080),
        Color(0xFFFA8072),
        Color(0xFF800080),
        Color(0xFF808000),
        Color(0xFF800000),
        Color(0xFF800040),
        Color(0xFF004080),
        Color(0xFF008040),
        Color(0xFF804000),
        Color(0xFF408000),
        Color(0xFF004040),
        Color(0xFF6A5ACD),
        Color(0xFFCD5C5C),
        Color(0xFF20B2AA),
        Color(0xFF9370DB),
        Color(0xFF32CD32),
        Color(0xFF7B68EE),
        Color(0xFF40E0D0),
        Color(0xFF6495ED),
        Color(0xFFDAA520)
    )

//    var animationPlayed by remember { mutableStateOf(false) }

    var lastValue = 0f

    // it is the diameter value of the Pie
//    val animateSize by animateFloatAsState(
//        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
//        animationSpec = tween(
//            durationMillis = animDuration,
//            delayMillis = 0,
//            easing = LinearOutSlowInEasing
//        )
//    )

    // if you want to stabilize the Pie Chart you can use value -90f
    // 90f is used to complete 1/4 of the rotation
//    val animateRotation by animateFloatAsState(
//        targetValue = if (animationPlayed) 90f * 11f else 0f,
//        animationSpec = tween(
//            durationMillis = animDuration,
//            delayMillis = 0,
//            easing = LinearOutSlowInEasing
//        )
//    )

    // to play the animation only once when the function is Created or Recomposed
//    LaunchedEffect(key1 = true) {
//        animationPlayed = true
//    }

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Pie Chart using Canvas Arc
        Row(
            modifier = Modifier
                .padding(0.dp, 25.dp)
                .weight(1f),
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
//                    .rotate(animateRotation)
            ) {
                // draw each Arc for each data entry in Pie Chart
                floatValue.forEachIndexed { index, value ->
                    drawArc(
                        color = colors[index],
                        lastValue,
                        value,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )
                    lastValue += value
                }
            }
        }

        // To see the data in more structured way
        // Compose Function in which Items are showing data
//        DetailsPieChart(
//            modifier = Modifier.weight(1f),
//            data = data,
//            colors = colors
//        )
        Column(
            modifier = Modifier
                .wrapContentWidth()
        ) {
            // create the data items
            data.values.forEachIndexed { index, value ->
                DetailsPieChartItem(
                    data = Pair(data.keys.elementAt(index), (value.toDouble()/total)*100),
                    color = colors[index]
                )
            }

        }

    }

}

@Composable
fun DetailsPieChartItem(
    data: Pair<String, Double>,
    height: Dp = 8.dp,
    color: Color
) {
    val percentage = DecimalFormat("#.#").format(data.second)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp, 1.dp)
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
//view rawPieChart hosted with ❤ by GitHub

@Preview(showBackground = true)
@Composable
fun PieChartPreview() {
    CustomPieChart(
        data = mapOf(
            Pair("Sample-1", 150),
            Pair("Sample-2", 120),
            Pair("Sample-3", 110),
            Pair("Sample-4", 170),
            Pair("Sample-5", 120)
        ),
        total = 670
    )
}