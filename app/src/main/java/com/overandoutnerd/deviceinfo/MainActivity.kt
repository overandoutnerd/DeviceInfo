package com.overandoutnerd.deviceinfo

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.overandoutnerd.deviceinfo.settings.SettingsViewModel
import com.overandoutnerd.deviceinfo.settings.AppTheme
import com.overandoutnerd.deviceinfo.settings.UserSettings
import com.overandoutnerd.deviceinfo.settings.UserSettingsImpl
import com.overandoutnerd.deviceinfo.ui.theme.DeviceInfoAppTheme
import com.google.android.gms.ads.MobileAds

class MainActivity: ComponentActivity() {

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 123
    }

    private lateinit var userSettings: UserSettings
    lateinit var viewModel: SettingsViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            requestPermissions(arrayOf(Manifest.permission.QUERY_ALL_PACKAGES))
        }
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        userSettings = UserSettingsImpl(this)
        enableEdgeToEdge()
        if (this.resources.getBoolean(R.bool.show_ads)){
            MobileAds.initialize(this)
        }
        setContent {
            val theme by viewModel.themeFlow.collectAsState()
            Log.e("Main Activity", "Theme: $theme")
            val useDarkColors = when (theme) {
                AppTheme.MODE_SYSTEM -> isSystemInDarkTheme()
                AppTheme.MODE_DAY -> false
                AppTheme.MODE_NIGHT -> true
            }
            val navController = rememberNavController()
            DeviceInfoAppTheme(darkTheme = useDarkColors) {
                DeviceInfoApp(
                    context = this,
                    navController = navController,
                    settingsViewModel = viewModel,
                    themeViewModel = viewModel.themeViewModel,
                    selectedTheme = theme,
                    hasUsageStatsPermission = { shouldAsk ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            checkUsageStatsPermission(shouldAsk)
                        } else {
                            false
                        }
                    },
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)
                )
            }
        }
    }

    private fun requestPermissions(permissions: Array<String>) {
        if (!arePermissionsGranted(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkUsageStatsPermission(shouldAsk: Boolean): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        var granted = mode == AppOpsManager.MODE_ALLOWED
        if (!granted && shouldAsk) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                granted = result.resultCode == Activity.RESULT_OK
            }
            requestPermissionLauncher.launch(intent)
        }
        return granted
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (::viewModel.isInitialized) {
            viewModel.loadSettingsItems(this)
        }
    }

}