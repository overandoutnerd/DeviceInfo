package com.overandoutnerd.deviceinfo.appsanalyze.signature

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.overandoutnerd.deviceinfo.appsanalyze.AnalyzeComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class SignatureRepository(private val context: Context) {

    suspend fun fetchDetails(): List<AnalyzeComponent<String>> {
        return withContext(Dispatchers.IO) {
            val signatures: MutableList<AnalyzeComponent<String>> = mutableListOf()
            try {
                val packageManager = context.packageManager
                val signaturesMap = mutableMapOf<String, Int>()

                val installedPackages = packageManager.getInstalledPackages(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        PackageManager.GET_SIGNING_CERTIFICATES
                    } else {
                        PackageManager.GET_SIGNATURES
                    }
                )

                for (packageInfo in installedPackages) {
                    val signatureAlgorithms = getSignatureAlgorithms(packageInfo)
                    for (algorithm in signatureAlgorithms) {
                        signaturesMap[algorithm] = (signaturesMap[algorithm] ?: 0) + 1
                    }
                }

                val totalApps = installedPackages.size

                for ((algorithm, count) in signaturesMap) {
                    val icon = context.getDrawable(com.overandoutnerd.deviceinfo.R.drawable.ic_approval)
                        ?: context.getDrawable(com.overandoutnerd.deviceinfo.R.drawable.ic_android_filled)!!

                    val percentage = (count.toDouble() / totalApps.toDouble()) * 100
                    signatures.add(
                        AnalyzeComponent(
                            id = algorithm,
                            title = algorithm,
                            icon = icon,
                            appCount = count,
                            percentage = percentage.toFloat()
                        )
                    )
                }

                signatures.sortedByDescending { it.appCount }
            } catch (e: Exception) {
                Log.e("SignatureRepository", "Failed to fetch details: ${e.message}")
                signatures
            }
        }
    }

    private fun getSignatureAlgorithms(packageInfo: PackageInfo): Set<String> {
        val algorithms = mutableSetOf<String>()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signingInfo = packageInfo.signingInfo
                if (signingInfo != null) {
                    val signatures = if (signingInfo.hasMultipleSigners()) {
                        signingInfo.apkContentsSigners
                    } else {
                        signingInfo.signingCertificateHistory
                    }
                    for (signature in signatures) {
                        parseSignature(signature.toByteArray())?.sigAlgName?.let { algorithms.add(it) }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures?.forEach { signature ->
                    parseSignature(signature.toByteArray())?.sigAlgName?.let { algorithms.add(it) }
                }
            }
        } catch (e: Exception) {
            Log.e("SignatureRepository", "Error getting algorithms for ${packageInfo.packageName}: ${e.message}")
        }
        return algorithms
    }

    private fun parseSignature(signature: ByteArray): X509Certificate? {
        return try {
            val certFactory = CertificateFactory.getInstance("X.509")
            certFactory.generateCertificate(ByteArrayInputStream(signature)) as? X509Certificate
        } catch (e: Exception) {
            null
        }
    }

}