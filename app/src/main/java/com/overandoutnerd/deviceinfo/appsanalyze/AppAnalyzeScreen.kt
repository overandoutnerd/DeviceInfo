package com.overandoutnerd.deviceinfo.appsanalyze

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.overandoutnerd.deviceinfo.appsanalyze.installers.InstallerScreen
import com.overandoutnerd.deviceinfo.appsanalyze.minimumapi.MinimumApiScreen
import com.overandoutnerd.deviceinfo.appsanalyze.signature.SignatureScreen
import com.overandoutnerd.deviceinfo.appsanalyze.targetapi.TargetApiScreen
import com.overandoutnerd.deviceinfo.home.ContextViewModelFactory
import com.overandoutnerd.deviceinfo.ui.components.CategoryIconTabRow
import com.kyant.backdrop.Backdrop

@Composable
fun AppsAnalyzeScreen(
    context: Context,
    scrollFraction: Float = 0f,
    backdrop: Backdrop
){

    val viewModel: AnalyzeComponentsViewModel = viewModel(factory = ContextViewModelFactory(context))

    val selectedTabItem by viewModel.selectedTabItem

    Column() {
        val pagerState = rememberPagerState {
            viewModel.tabItemsState.value.size
        }
        LaunchedEffect(selectedTabItem) {
            pagerState.animateScrollToPage(selectedTabItem)
        }
        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if(!pagerState.isScrollInProgress) {
                viewModel.updateSelectedTabItem(pagerState.currentPage)
            }
        }
        CategoryIconTabRow(
            itemCount = viewModel.tabItemsState.value.size,
            selectedIndex = selectedTabItem,
            onTabSelected = { viewModel.updateSelectedTabItem(it) },
            icon = { index -> viewModel.tabItemsState.value[index].icon },
            title = { index -> stringResource(id = viewModel.tabItemsState.value[index].title) },
            contentDescription = { index -> stringResource(id = viewModel.tabItemsState.value[index].title) },
            currentPageOffset = { pagerState.currentPage + pagerState.currentPageOffsetFraction },
            scrollFraction = scrollFraction
        )
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(state = pagerState) { index ->
                when(index) {
                    0 -> TargetApiScreen(
                        context = context,
                        backdrop = backdrop,
                        onChipClick = {
                            viewModel.showAppsForItem(AnalyzeTab.ANALYZE_TAB_TARGET, it)
                        }
                    ) { }
                    1 -> MinimumApiScreen(
                        context = context,
                        backdrop = backdrop,
                        onChipClick = {
                            viewModel.showAppsForItem(AnalyzeTab.ANALYZE_TAB_MINIMUM, it)
                        }
                    ) { }
                    2 -> InstallerScreen(
                        context = context,
                        backdrop = backdrop,
                        onChipClick = {
                            viewModel.showAppsForItem(AnalyzeTab.ANALYZE_TAB_INSTALLERS, it)
                        }
                    ) {  }
                    3 -> SignatureScreen(
                        context = context,
                        backdrop = backdrop,
                        onChipClick = {
                            viewModel.showAppsForItem(AnalyzeTab.ANALYZE_TAB_SIGNATURES, it)
                        }
                    ) {  }
                    else -> TargetApiScreen(
                        context = context,
                        backdrop = backdrop,
                        onChipClick = {
                            viewModel.showAppsForItem(AnalyzeTab.ANALYZE_TAB_TARGET, it)
                        }
                    ) { }
                }
            }
        }
    }
}