package com.overandoutnerd.deviceinfo.home.apps.details

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.overandoutnerd.deviceinfo.R
import com.overandoutnerd.deviceinfo.ads.LargeNativeAdItem
import com.overandoutnerd.deviceinfo.ads.NativeAdView
import com.overandoutnerd.deviceinfo.home.UserDeviceDetailsProperty
import com.overandoutnerd.deviceinfo.ui.components.DetailListItem
import com.overandoutnerd.deviceinfo.ui.components.AppDetailsHeader
import com.overandoutnerd.deviceinfo.ui.components.AppDetailsListItemClickable
import com.overandoutnerd.deviceinfo.ui.components.RoundedCornerBox
import com.farimarwat.composenativeadmob.nativead.rememberNativeAdState

data class AppDetailsComponent(
    var title: String,
    var size: Int,
    var flag: Int,
    var packageName: String,
    var action: (title: String, packageName: String, flag: Int) -> Unit
)

@Composable
fun AppDetailsHomeScreen(
    appDetails: AppDetails?,
    navigateToComponents: (String, String, Int) -> Unit
){

    val displayedAppDetails by remember {
        mutableStateOf(appDetails)
    }

    val appDetailsList: MutableList<UserDeviceDetailsProperty> = mutableListOf()
    if (displayedAppDetails != null) {
        val appName = UserDeviceDetailsProperty(stringResource(id = R.string.app_details_item_title_app_name), displayedAppDetails!!.name)
        appDetailsList.add(appName)
        val packageName = UserDeviceDetailsProperty(stringResource(id = R.string.app_details_item_title_package_name), displayedAppDetails!!.packageName)
        appDetailsList.add(packageName)
        val version = UserDeviceDetailsProperty(stringResource(id = R.string.app_details_item_title_version), displayedAppDetails!!.version)
        appDetailsList.add(version)
        val minSdk = UserDeviceDetailsProperty(stringResource(id = R.string.app_details_item_title_minimum_sdk), displayedAppDetails!!.minSdk.toString())
        appDetailsList.add(minSdk)
        val targetSdk = UserDeviceDetailsProperty(stringResource(id = R.string.app_details_item_title_target_sdk), displayedAppDetails!!.targetSdk.toString())
        appDetailsList.add(targetSdk)
        val installDate = UserDeviceDetailsProperty(stringResource(id = R.string.app_details_item_title_install_date), displayedAppDetails!!.installDate.toString())
        appDetailsList.add(installDate)
        val lastUpdate = UserDeviceDetailsProperty(stringResource(id = R.string.app_details_item_title_last_update), displayedAppDetails!!.lastUpdate.toString())
        appDetailsList.add(lastUpdate)
    }

    if (appDetails != null){
        AppDetailsScreen(
            appDetails = appDetails,
            list = appDetailsList,
            navigateToComponents = navigateToComponents
        )
    }
}

@Composable
fun AppDetailsScreen(
    appDetails: AppDetails,
    list: MutableList<UserDeviceDetailsProperty>,
    navigateToComponents: (String, String, Int) -> Unit
    ) {

    val context = LocalContext.current
    val adState = rememberNativeAdState(
        context = context,
        adUnitId = "ca-app-pub-3940256099942544/2247696110",
        refreshInterval = 0
    )

    val components: MutableList<AppDetailsComponent> = mutableListOf()
    val baseComponents = listOf(
        AppDetailsComponent(stringResource(id = R.string.app_component_item_title_permissions), appDetails.totalPermissions, PackageManager.GET_PERMISSIONS, appDetails.packageName, navigateToComponents),
        AppDetailsComponent(stringResource(id = R.string.app_component_item_title_providers), appDetails.totalProviders, PackageManager.GET_PROVIDERS, appDetails.packageName, navigateToComponents),
        AppDetailsComponent(stringResource(id = R.string.app_component_item_title_receivers), appDetails.totalReceivers, PackageManager.GET_RECEIVERS, appDetails.packageName, navigateToComponents),
        AppDetailsComponent(stringResource(id = R.string.app_component_item_title_services), appDetails.totalServices, PackageManager.GET_SERVICES, appDetails.packageName, navigateToComponents)
    )
    baseComponents.forEach {
        if(it.size > 0) {
            components.add(it)
        }
    }

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        LazyColumn {
            item {
                AppDetailsHeader(
                    app = appDetails
                )
            }
            if (components.isNotEmpty()) {
                item {
                    RoundedCornerBox(innerPadding = PaddingValues(0.dp)) {
                        Column {
                            components.forEach { item ->
                                AppDetailsListItemClickable(
                                    component = item,
                                    packageName = appDetails.packageName,
                                )
                            }
                        }
                    }
                }
            }
            if (adState != null) {
                item {
                    RoundedCornerBox(outerPadding = PaddingValues(12.dp, 3.dp, 12.dp, 12.dp)) {
                        NativeAdView(ad = adState) { ad, view ->
                            LargeNativeAdItem(modifier = Modifier.padding(5.dp), loadedAd = ad, composeView = view)
                        }
                    }
                }
            }
            item {
                RoundedCornerBox(outerPadding = PaddingValues(12.dp, 3.dp)) {
                    Column {
                        list.forEach { item ->
                            DetailListItem(item = item)
                            if (item != list.last()) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}