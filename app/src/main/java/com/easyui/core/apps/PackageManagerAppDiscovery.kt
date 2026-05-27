package com.easyui.core.apps

import android.content.Context
import android.content.Intent
import android.os.Build
import android.content.pm.ResolveInfo

class PackageManagerAppDiscovery(
    private val context: Context,
) : AppDiscovery {

    private val pm = context.packageManager

    override fun discoverLaunchableApps(): List<LaunchableApp> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

        val resolveInfos = queryLaunchableActivities(intent)
        val apps = resolveInfos.mapNotNull { ri -> ri.toLaunchableAppOrNull() }
            .filterNot { it.packageName == context.packageName }

        return sortAppsByLabel(apps)
    }

    private fun queryLaunchableActivities(intent: Intent): List<ResolveInfo> {
        return if (Build.VERSION.SDK_INT >= 33) {
            pm.queryIntentActivities(
                intent,
                android.content.pm.PackageManager.ResolveInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }
    }

    private fun ResolveInfo.toLaunchableAppOrNull(): LaunchableApp? {
        val ai = activityInfo ?: return null
        val label = safeLabel(loadLabel(pm), fallback = ai.packageName)

        return LaunchableApp(
            label = label,
            packageName = ai.packageName,
            activityName = ai.name,
        )
    }
}
