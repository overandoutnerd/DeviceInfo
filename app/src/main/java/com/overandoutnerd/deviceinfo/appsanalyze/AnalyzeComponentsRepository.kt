package com.overandoutnerd.deviceinfo.appsanalyze

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds

class AnalyzeComponentsRepository(private val context: Context) {
    private val cachedPackages = mutableMapOf<Int, List<PackageInfo>>()
    private val mutex = Mutex()
    private val packageManagerExecutor = Executors.newCachedThreadPool()

    private suspend fun <T> withHardTimeout(timeoutMs: Long, block: () -> T): T? {
        val deferred = CompletableDeferred<T>()
        packageManagerExecutor.execute {
            try {
                deferred.complete(block())
            } catch (e: Throwable) {
                deferred.completeExceptionally(e)
            }
        }
        return withTimeoutOrNull(timeoutMs.milliseconds) { deferred.await() }
    }

    private suspend fun getPackages(flags: Int): List<PackageInfo> {
        return mutex.withLock {
            cachedPackages[flags]?.let { return@withLock it }

            val packages = try {
                withHardTimeout(8_000L) {
                    context.packageManager.getInstalledPackages(flags)
                } ?: run {
                    Log.e("AnalyzeComponentRepository", "Method: getPackages(), Error: timed out")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("AnalyzeComponentRepository", "Method: getPackages(), Error: ${e.message}")
                emptyList()
            }
            cachedPackages[flags] = packages
            packages
        }
    }

    private fun resolveInstallerId(packageInfo: PackageInfo, packageManager: PackageManager): String {
        val installerPackageName = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                packageManager.getInstallSourceInfo(packageInfo.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstallerPackageName(packageInfo.packageName)
            }
        } catch (e: Exception) {
            null
        }

        return when {
            installerPackageName == "com.android.shell" -> "debug"
            installerPackageName != null -> installerPackageName
            (packageInfo.applicationInfo?.flags ?: 0) and android.content.pm.ApplicationInfo.FLAG_SYSTEM != 0 -> "system"
            else -> "direct"
        }
    }

    private fun resolveSignatureAlgorithms(packageInfo: PackageInfo): List<String> {
        val algorithms = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val signingInfo = packageInfo.signingInfo
            if (signingInfo != null) {
                val signatures = if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo.signingCertificateHistory
                }

                for (signature in signatures) {
                    val cert = parseSignature(signature.toByteArray())
                    algorithms.add(cert?.sigAlgName ?: "Unknown")
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val signatures = packageInfo.signatures
            if (signatures != null) {
                for (signature in signatures) {
                    val cert = parseSignature(signature.toByteArray())
                    algorithms.add(cert?.sigAlgName ?: "Unknown")
                }
            }
        }
        return algorithms.distinct()
    }

    private fun parseSignature(signature: ByteArray): X509Certificate? {
        val certFactory = CertificateFactory.getInstance("X.509")
        return try {
            certFactory.generateCertificate(ByteArrayInputStream(signature)) as? X509Certificate
        } catch (e: Exception) {
            null
        }
    }

    suspend fun <T> getAppsForItem(tab: AnalyzeTab, item: AnalyzeComponent<T>): List<AnalyzeAppInfo> {
        return withContext(Dispatchers.IO) {
            val flags = if (tab == AnalyzeTab.ANALYZE_TAB_SIGNATURES) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) PackageManager.GET_SIGNING_CERTIFICATES else PackageManager.GET_SIGNATURES
            } else {
                PackageManager.GET_META_DATA
            }
            val packages = getPackages(flags)
            val pm = context.packageManager

            val matches = when (tab) {
                AnalyzeTab.ANALYZE_TAB_TARGET ->
                    packages.filter { it.applicationInfo?.targetSdkVersion == item.id }
                AnalyzeTab.ANALYZE_TAB_MINIMUM ->
                    packages.filter { it.applicationInfo?.minSdkVersion == item.id }
                AnalyzeTab.ANALYZE_TAB_INSTALLERS ->
                    packages.filter { resolveInstallerId(it, pm) == item.id }
                AnalyzeTab.ANALYZE_TAB_SIGNATURES ->
                    packages.filter { resolveSignatureAlgorithms(it).any { alg -> alg == item.id } }
            }

            matches.mapNotNull { pkg ->
                val appInfo = pkg.applicationInfo ?: return@mapNotNull null
                AnalyzeAppInfo(
                    packageName = pkg.packageName,
                    appName = appInfo.loadLabel(pm).toString(),
                    versionName = pkg.versionName,
                    icon = appInfo.loadIcon(pm)
                )
            }.sortedBy { it.appName.lowercase() }
        }
    }
}
