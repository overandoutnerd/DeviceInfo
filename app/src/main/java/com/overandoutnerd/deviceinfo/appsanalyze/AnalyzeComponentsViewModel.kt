package com.overandoutnerd.deviceinfo.appsanalyze

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.GetApp
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.models.TabItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class AnalyzeComponentsViewModel(context: Context): ViewModel() {

    private val repository = AnalyzeComponentsRepository(context)


    private val tabItems = listOf(
        TabItem(R.string.analyze_tabs_item_title_target, context.getString(R.string.analyze_target_api_flag), icon = Icons.Rounded.TrackChanges),
        TabItem(R.string.analyze_tabs_item_title_minimum, context.getString(R.string.analyze_minimum_api_flag), icon = Icons.Rounded.ArrowDownward),
        TabItem(R.string.analyze_tabs_item_title_installer, context.getString(R.string.analyze_installer_flag), icon = Icons.Rounded.GetApp),
        TabItem(R.string.analyze_tabs_item_title_signature, context.getString((R.string.analyze_signature_flag)), icon = Icons.Rounded.Fingerprint)
    )

    private val _tabItemsState = mutableStateOf(tabItems)
    val tabItemsState: State<List<TabItem>> = _tabItemsState

    private val _selectedTabItem = mutableIntStateOf(0)
    var selectedTabItem: State<Int> = _selectedTabItem

    private val _appsSheetState = mutableStateOf(AnalyzeAppsSheetState())
    val appsSheetState: State<AnalyzeAppsSheetState> = _appsSheetState

    // Cache for apps list to avoid refetching the same item's apps
    private val appsCache = ConcurrentHashMap<String, List<AnalyzeAppInfo>>()

    init {

    }

    fun updateSelectedTabItem(index: Int) {
        _selectedTabItem.intValue = index
    }

    fun <T> showAppsForItem(tab: AnalyzeTab, item: AnalyzeComponent<T>) {
        val cacheKey = "$tab:${item.id}"
        
        _appsSheetState.value = AnalyzeAppsSheetState(
            visible = true,
            loading = !appsCache.containsKey(cacheKey),
            title = item.title,
            apps = appsCache[cacheKey] ?: emptyList()
        )

        if (!appsCache.containsKey(cacheKey)) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val apps = repository.getAppsForItem(tab, item)
                    appsCache[cacheKey] = apps
                    _appsSheetState.value = _appsSheetState.value.copy(
                        loading = false,
                        apps = apps
                    )
                } catch (e: Exception) {
                    _appsSheetState.value = _appsSheetState.value.copy(
                        loading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun dismissAppsSheet() {
        _appsSheetState.value = _appsSheetState.value.copy(visible = false)
    }
}
