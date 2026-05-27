package com.easyui.core.apps

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class PackageManagerAppLauncher(
    private val context: Context,
) : AppLauncher {

    override fun launch(app: LaunchableApp): LaunchResult {
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(ComponentName(app.packageName, app.activityName))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return try {
            context.startActivity(intent)
            LaunchResult.Success
        } catch (_: ActivityNotFoundException) {
            LaunchResult.Failure("App not found.")
        } catch (_: SecurityException) {
            LaunchResult.Failure("Unable to launch app.")
        } catch (_: RuntimeException) {
            // Be defensive: malformed components or OEM quirks shouldn't crash the launcher.
            LaunchResult.Failure("Unable to launch app.")
        }
    }
}
