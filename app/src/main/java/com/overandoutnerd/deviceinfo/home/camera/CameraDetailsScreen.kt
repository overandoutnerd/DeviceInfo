package com.overandoutnerd.deviceinfo.home.camera

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.overandoutnerd.deviceinfo.home.ContextViewModelFactory
import com.overandoutnerd.deviceinfo.ui.components.DetailListItem
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.overandoutnerd.deviceinfo.ui.theme.BottomBarContentClearance

@Composable
fun CameraDetailsScreen(
    context: Context
){
    val viewModel: CameraDetailsViewModel = viewModel(factory = ContextViewModelFactory(context))
    val viewState by viewModel.cameraDetailsState

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            viewState.loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            viewState.error != null -> Text(text = "${viewState.error}")
            else -> {
                var selectedCameraItem by remember {
                    mutableIntStateOf(0)
                }
                val cameraList: MutableList<String> = mutableListOf()
                viewState.cameraDetails.forEach {
                    cameraList.add(it[0].value)
                }
                LazyColumn(
                    contentPadding = PaddingValues(bottom = BottomBarContentClearance)
                ) {
                    item {
                        RoundedCornerBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopStart
                        ) {
                            LazyRow(modifier = Modifier.padding(5.dp, 0.dp)) {
                                itemsIndexed(cameraList) { index, item ->
                                    FilterChip(
                                        selected = selectedCameraItem == index,
                                        onClick = { selectedCameraItem = index },
                                        label = { Text(text = item) },
                                        modifier = Modifier.padding(6.dp, 0.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                    item {
                        RoundedCornerBox {
                            Column {
                                viewState.cameraDetails[selectedCameraItem].forEach { item ->
                                    DetailListItem(item = item)
                                    if (item != viewState.cameraDetails[selectedCameraItem].last()) {
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}