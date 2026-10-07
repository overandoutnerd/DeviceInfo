package com.overandoutnerd.deviceinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.overandoutnerd.deviceinfo.home.storage.MemoryInfo
import com.overandoutnerd.deviceinfo.home.storage.MemoryUser

@Composable
fun MemoryCard(
    memoryInfo: MemoryInfo,
    modifier: Modifier = Modifier
) {
    RoundedCornerBox(
        modifier = modifier.fillMaxWidth(),
        outerPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            // Header: Title, Type & Usage Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = memoryInfo.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${memoryInfo.formattedUsed} used of ${memoryInfo.formattedTotal}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${memoryInfo.usedPercentage.toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Multi-segment progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    memoryInfo.users.forEachIndexed { index, user ->
                        val fraction = if (memoryInfo.total > 0) {
                            (user.usage.toFloat() / memoryInfo.total.toFloat()).coerceIn(0f, 1f)
                        } else 0f

                        if (fraction > 0f) {
                            val color = getCategoryColor(user.name, index)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(fraction)
                                    .background(color)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Users Breakdown List
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                memoryInfo.users.forEachIndexed { index, user ->
                    MemoryUserRow(
                        user = user,
                        totalMemory = memoryInfo.total,
                        color = getCategoryColor(user.name, index)
                    )
                }
            }
        }
    }
}

@Composable
private fun getCategoryColor(categoryName: String, index: Int): Color {
    return when {
        categoryName.startsWith("Apps", ignoreCase = true) || categoryName.equals("Used Space", ignoreCase = true) ->
            MaterialTheme.colorScheme.primary
        categoryName.equals("Cache", ignoreCase = true) ->
            MaterialTheme.colorScheme.secondary
        categoryName.contains("System", ignoreCase = true) ->
            MaterialTheme.colorScheme.tertiary
        categoryName.startsWith("Free", ignoreCase = true) ->
            MaterialTheme.colorScheme.outlineVariant
        else -> {
            val fallbackColors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.error
            )
            fallbackColors[index % fallbackColors.size]
        }
    }
}

@Composable
private fun MemoryUserRow(
    user: MemoryUser,
    totalMemory: Long,
    color: Color
) {
    val percentage = user.getPercentage(totalMemory)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user.formattedUsage,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "(${percentage.toInt()}%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}
