package com.overandoutnerd.deviceinfo.home.apps.details.appcomponents

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.overandoutnerd.deviceinfo.ui.AppComponentListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppComponentScreen(
    componentTitle: String,
    viewModel: AppComponentViewModel,
    onBackPressed: () -> Unit
) {
    val viewState by viewModel.viewState

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current
    val onBackPressedCallback = remember {
        object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }

    DisposableEffect(backPressedDispatcher) {
        backPressedDispatcher!!.onBackPressedDispatcher.addCallback(onBackPressedCallback)
        onDispose {
            onBackPressedCallback.remove()
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text(text = "App $componentTitle") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(it)
            .padding(5.dp)
        ){
            when{
                viewState.loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                viewState.error != null -> {
                    Text(text = "Error Occurred")
                }

                else -> {
                    LazyColumn {
                        items(viewState.componentList) { item ->
                            val modifier = when (item) {
                                viewState.componentList.first() -> Modifier.clip(
                                    RoundedCornerShape(16.dp, 16.dp, 4.dp, 4.dp)
                                )
                                viewState.componentList.last() -> Modifier.clip(
                                    RoundedCornerShape(4.dp, 4.dp, 16.dp, 16.dp)
                                )
                                else -> Modifier.clip(RoundedCornerShape(4.dp))
                            }
                            AppComponentListItem(
                                permission = item,
                                modifier = modifier
                            )
                        }
                    }
                }
            }
        }
    }
}