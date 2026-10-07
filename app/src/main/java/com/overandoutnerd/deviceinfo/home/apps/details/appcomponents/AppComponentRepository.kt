package com.overandoutnerd.deviceinfo.home.apps.details.appcomponents

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class SimpleComponent(
    val name: String,
    val label: String,
    val componentPackage: String,
    val description: String,
    val icon: Drawable? = null
)

class AppComponentRepository(private val context: Context) {

    suspend fun getComponents(packageName: String, flag: Int): List<SimpleComponent> {
        return withContext(Dispatchers.IO) {
            val packageManager = context.packageManager
            var componentsList: List<SimpleComponent> = emptyList()
            try {
                componentsList = when (flag) {
                    PackageManager.GET_PERMISSIONS -> getPermissions(packageName)
                    PackageManager.GET_PROVIDERS -> getProviders(packageName)
                    PackageManager.GET_RECEIVERS -> getReceivers(packageName)
                    PackageManager.GET_SERVICES -> getServices(packageName)
                    else -> emptyList()
                }
                componentsList
            } catch (e: Exception) {
                componentsList
            }
        }
    }
    private fun getPermissions(packageName: String): List<SimpleComponent> {
        val packageManager = context.packageManager
        val permissionsList: MutableList<SimpleComponent> = mutableListOf()
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val permissions = packageInfo.requestedPermissions ?: arrayOf()

            for (permissionName in permissions) {
                try {
                    val permissionInfo = packageManager.getPermissionInfo(permissionName, 0)
                    val permission = SimpleComponent(
                        name = permissionName,
                        label = makeReadableTitle(permissionName),
                        componentPackage = permissionName.substringBeforeLast('.'),
                        description = permissionInfo.loadDescription(packageManager).toString() ?: "",
                        permissionInfo.loadIcon(packageManager)
                    )
                    Log.e("Permission", "Name: ${permission.name}, Label: ${permission.label}, Description: ${permission.description}")
                    permissionsList.add(permission)
                } catch(e: Exception) {
                    Log.e("AppComponentRepository", "Error Occurred: ${e.message}")
                    val permission = SimpleComponent(
                        name = permissionName,
                        label = makeReadableTitle(permissionName),
                        componentPackage = permissionName.substringBeforeLast('.'),
                        description = "Description not available",
                        null
                    )
                    Log.e("Permission", "Name: ${permission.name}, Label: ${permission.label}, Description: ${permission.description}")
                    permissionsList.add(permission)
                }
            }
            permissionsList
        } catch(e: Exception){
            Log.e("AppComponentRepository", "$${e.message}")
            permissionsList
        }
    }

    private fun getProviders(packageName: String): List<SimpleComponent> {
        val packageManager = context.packageManager
        val providersList: MutableList<SimpleComponent> = mutableListOf()
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PROVIDERS)
            val providers = packageInfo.providers ?: arrayOf()

            for (provider in providers) {
                val providerObject = SimpleComponent(
                    name = provider.name,
                    label = getReadableProviderName(provider.name),
                    componentPackage = provider.name.substringBeforeLast('.'),
                    description = provider.loadLabel(packageManager).toString() ?: ""
                )
                Log.e("Provider", "Name: ${providerObject.name}, Label: ${providerObject.label}")
                providersList.add(providerObject)
            }
            providersList
        } catch(e: Exception) {
            providersList
        }
    }

    private fun getReceivers(packageName: String): List<SimpleComponent> {
        val packageManager = context.packageManager
        val receiversList: MutableList<SimpleComponent> = mutableListOf()
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_RECEIVERS)
            val receivers = packageInfo.receivers ?: arrayOf()

            for (receiver in receivers) {
                val receiverObject = SimpleComponent(
                    name = receiver.name,
                    label = receiver.name,
                    componentPackage = receiver.name.substringBeforeLast('.'),
                    description = receiver.loadLabel(packageManager).toString() ?: ""
                )
                Log.e("Receiver", "Name: ${receiverObject.name}, Label: ${receiverObject.label}")
                receiversList.add(receiverObject)
            }
            receiversList
        } catch(e: Exception) {
            receiversList
        }
    }

    private fun getServices(packageName: String): List<SimpleComponent> {
        val packageManager = context.packageManager
        val servicesList: MutableList<SimpleComponent> = mutableListOf()
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_RECEIVERS)
            val services = packageInfo.services ?: arrayOf()

            for (service in services) {
                val serviceObject = SimpleComponent(
                    name = service.name,
                    label = service.name,
                    componentPackage = service.name.substringBeforeLast('.'),
                    description = service.loadLabel(packageManager).toString() ?: ""
                )
                Log.e("Service", "Name: ${serviceObject.name}, Label: ${serviceObject.label}")
                servicesList.add(serviceObject)
            }
            servicesList
        } catch(e: Exception) {
            servicesList
        }
    }

    private fun makeReadableTitle(original: String): String {
        val removeAfterLastDot = original.substringAfterLast('.')
        val replaceDashesWithSpaces = removeAfterLastDot.replace('_', ' ')
        val modified = replaceDashesWithSpaces.split(' ').joinToString(" ") { word ->
            word.lowercase().replaceFirstChar {
                it.uppercase()
            }
        }
        return modified
    }

    private fun getReadableProviderName(original: String): String {
        val removeAfterLastDot = original.substringAfterLast('.')
        return removeAfterLastDot.replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")
    }

}