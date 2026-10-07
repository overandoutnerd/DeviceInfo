package com.overandoutnerd.deviceinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 *  Contains default values to define a [RoundedCornerBox]
 *
 *  @constructor Create empty Rounded corner box defaults
 */
object RoundedCornerBoxDefaults {
    val shape = RoundedCornerShape(24.dp)
    val elevation = 3.dp
    val innerPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 14.dp
    )
    val outerPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 8.dp
    )
}

/**
 *  Composable for a rounded corner box to use as surface
 *
 *  @param modifier The modifier to be applied to the container
 *  @param colors The [RoundedCornerBoxColors] to use
 *  @param shape The shape to cut the box in
 *  @param elevation Soft drop-shadow applied behind the card, Samsung Health style
 *  @param contentAlignment The [Alignment] to align the content by
 *  @param innerPadding The [PaddingValues] to use inside box
 *  @param outerPadding The [PaddingValues] to use outside box
 *  @param content The content of the box
 */
@Composable
fun RoundedCornerBox(
    modifier: Modifier = Modifier,
    colors: RoundedCornerBoxColors = roundedCornerBoxColors(),
    shape: Shape = RoundedCornerBoxDefaults.shape,
    elevation: Dp = RoundedCornerBoxDefaults.elevation,
    contentAlignment: Alignment = Alignment.Center,
    innerPadding: PaddingValues = RoundedCornerBoxDefaults.innerPadding,
    outerPadding: PaddingValues = RoundedCornerBoxDefaults.outerPadding,
    content: @Composable BoxScope.() -> Unit
){
    Box(
        modifier = Modifier
            .padding(outerPadding)
            .then(
                if (elevation > 0.dp)
                    Modifier.shadow(elevation, shape, clip = false, ambientColor = Color.Black.copy(alpha = 0.12f), spotColor = Color.Black.copy(alpha = 0.12f))
                else Modifier
            )
            .clip(shape)
            .background(
                color = colors.background,
                shape = shape
            )
            .then(modifier)
            .padding(innerPadding),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

/**
 *  Contains the colors that define a [RoundedCornerBoxColors]
 */
data class RoundedCornerBoxColors(
    val background: Color
)

/**
 * Constructs the default [RoundedCornerBoxColors]
 *
 * @param background The background color of the box
 * @return
 */
@Composable
fun roundedCornerBoxColors(
    background: Color = MaterialTheme.colorScheme.surfaceContainerLowest
): RoundedCornerBoxColors = RoundedCornerBoxColors(
    background = background
)

