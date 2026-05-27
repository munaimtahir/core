package com.easyui.core.apps

import android.content.Context
import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.easyui.core.R

class PackageManagerAppIconLoader(
    private val context: Context,
) : AppIconLoader {

    private val pm = context.packageManager
    private val fallback: Drawable by lazy(LazyThreadSafetyMode.NONE) {
        requireNotNull(ContextCompat.getDrawable(context, R.drawable.ic_app_placeholder))
    }

    override fun loadIcon(app: LaunchableApp): Drawable {
        return try {
            pm.getActivityIcon(ComponentName(app.packageName, app.activityName))
        } catch (_: PackageManager.NameNotFoundException) {
            fallback
        } catch (_: RuntimeException) {
            fallback
        }
    }
}
