package com.overandoutnerd.deviceinfo.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.ui.components.GlassDialog
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.overandoutnerd.deviceinfo.ui.shapes.Capsule
import com.overandoutnerd.deviceinfo.ui.theme.BottomBarContentClearance
import com.overandoutnerd.deviceinfo.ui.theme.LocalIsDarkTheme
import com.overandoutnerd.deviceinfo.ui.theme.healthAccentBlue
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.shapes.RoundedRectangle

@Composable
fun SettingsScreen(
    onClickSettingItem: (String) -> Unit,
    viewModel: SettingsViewModel
) {
    val settingsItems by viewModel.settingsItemsState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = BottomBarContentClearance)
        ) {
            item {
                RoundedCornerBox {
                    Column {
                        settingsItems.items.forEach { item ->
                            SettingsScreenItem(
                                item = item,
                                onClickSettingItem = onClickSettingItem
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreenItem(
    item: SettingsItem,
    modifier: Modifier = Modifier,
    onClickSettingItem: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .clip(RoundedCornerShape(26.dp))
            .clickable { onClickSettingItem(item.flag) }
            .padding(0.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(50.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(27)),
            painter = painterResource(id = item.icon),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            contentDescription = null
        )
        Column(modifier = Modifier.padding(5.dp)) {
            Text(
                text = item.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            item.subTitle?.let {
                Text(
                    text = it,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

    }
}

@Composable
fun BoxScope.GlassThemeChooserDialog(
    backdrop: Backdrop,
    visible: Boolean,
    viewModel: ThemeViewModel,
    selectedTheme: AppTheme,
    onDismiss: () -> Unit
) {
    val isLightTheme = !LocalIsDarkTheme.current
    val contentColor = if (isLightTheme) Color.Black else Color.White
    val containerColor =
        if (isLightTheme) Color(0xFFFAFAFA).copy(0.6f)
        else Color(0xFF121212).copy(0.4f)

    GlassDialog(
        backdrop = backdrop,
        visible = visible,
        onDismiss = onDismiss
    ) {
        val themeItems = AppTheme.entries.map { theme ->
            RadioButtonItem(
                id = theme.ordinal,
                title = stringResource(id = theme.displayNameRes)
            )
        }
        var selectedItem by remember(selectedTheme) {
            mutableIntStateOf(selectedTheme.ordinal)
        }

        BasicText(
            text = stringResource(id = R.string.settings_theme_dialog_title),
            Modifier.padding(28f.dp, 24f.dp, 28f.dp, 12f.dp),
            style = TextStyle(contentColor, fontSize = 24f.sp, fontWeight =  FontWeight.Medium)
        )

        RadioGroup(
            items = themeItems,
            selected = selectedItem,
            onItemSelect = { id -> selectedItem = id },
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            Modifier
                .padding(24f.dp, 12f.dp, 24f.dp, 24f.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16f.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                Modifier
                    .clip(Capsule())
                    .background(containerColor.copy(0.2f))
                    .clickable { onDismiss() }
                    .height(48f.dp)
                    .weight(1f)
                    .padding(horizontal = 16f.dp),
                horizontalArrangement = Arrangement.spacedBy(4f.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = stringResource(id = R.string.settings_theme_dialog_cancel),
                    style = TextStyle(contentColor, fontSize = 16f.sp)
                )
            }
            Row(
                Modifier
                    .clip(Capsule())
                    .background(healthAccentBlue)
                    .clickable {
                        viewModel.updateTheme(AppTheme.fromOrdinal(selectedItem))
                        onDismiss()
                    }
                    .height(48f.dp)
                    .weight(1f)
                    .padding(horizontal = 16f.dp),
                horizontalArrangement = Arrangement.spacedBy(4f.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = stringResource(id = R.string.settings_theme_dialog_save),
                    style = TextStyle(Color.White, 16f.sp)
                )
            }
        }
    }
}

@Composable
fun RadioGroup(
    items: Iterable<RadioButtonItem>,
    selected: Int,
    onItemSelect: ((Int) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        items.forEach { item ->
            RadioGroupItem(
                item = item,
                selected = selected == item.id,
                onClick = { onItemSelect?.invoke(item.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RadioGroupItem(
    item: RadioButtonItem,
    selected: Boolean,
    onClick: ((Int) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = { onClick?.invoke(item.id) },
                role = Role.RadioButton
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = item.title, style = MaterialTheme.typography.bodyMedium)
    }
}