package com.easyui.core.apps

import android.graphics.drawable.Drawable

interface AppIconLoader {
    fun loadIcon(app: LaunchableApp): Drawable
}

