package com.overandoutnerd.deviceinfo.home.apps

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.home.ContextViewModelFactory
import com.overandoutnerd.deviceinfo.home.apps.all.SimpleAppsListViewModel
import com.overandoutnerd.deviceinfo.home.apps.system.SystemAppsListViewModel
import com.overandoutnerd.deviceinfo.home.apps.user.UserAppsListViewModel
import com.overandoutnerd.deviceinfo.ui.InstalledApp
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.overandoutnerd.deviceinfo.ui.theme.BottomBarContentClearance

data class ViewModelState(
    var loading: Boolean = true,
    var apps: List<SimpleAppDetails> = emptyList(),
    var error: String? = null
)

@Composable
fun InstalledAppsHomeScreen(
    context: Context,
    navigateToAppDetails: (SimpleAppDetails) -> Unit
){
    val filterChips = listOf(
        stringResource(id = R.string.home_tab_apps_chips_item_title_all),
        stringResource(id = R.string.home_tab_apps_chips_item_title_user),
        stringResource(id = R.string.home_tab_apps_chips_item_title_system)
    )
    var selectedItem by remember {
        mutableStateOf(0)
    }
    
    val allAppsViewModel: SimpleAppsListViewModel = viewModel(factory = ContextViewModelFactory(context))
    val userAppsViewModel: UserAppsListViewModel = viewModel(factory = ContextViewModelFactory(context))
    val systemAppsViewModel: SystemAppsListViewModel = viewModel(factory = ContextViewModelFactory(context))

    val viewModelState = when(selectedItem){
        1 -> {
            val viewState by userAppsViewModel.userAppsListState
            ViewModelState(viewState.loading, viewState.userApps, viewState.error)
        }
        2 -> {
            val viewState by systemAppsViewModel.systemAppsListState
            ViewModelState(viewState.loading, viewState.systemApps, viewState.error)
        }
        else -> {
            val viewState by allAppsViewModel.simpleAppsListState
            ViewModelState(viewState.loading, viewState.simpleApps, viewState.error)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = BottomBarContentClearance)
    ) {
        item {
            RoundedCornerBox(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopStart
            ) {
                LazyRow(modifier = Modifier.padding(5.dp, 0.dp)) {
                    itemsIndexed(filterChips){ index, item ->
                        FilterChip(
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                            },
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

        if (viewModelState.loading) {
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else if (viewModelState.error != null) {
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error Occurred: ${viewModelState.error}")
                }
            }
        } else {
            items(viewModelState.apps) { item ->
                InstalledApp(
                    installedApp = item,
                    navigateToAppDetails = navigateToAppDetails,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
