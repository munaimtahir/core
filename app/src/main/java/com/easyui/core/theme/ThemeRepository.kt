package com.easyui.core.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.easyui.core.storage.coreDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeRepository(
    private val context: Context,
) {
    private val paletteKey = stringPreferencesKey("theme_palette")
    private val textSizeKey = stringPreferencesKey("text_size")
    private val iconSizeKey = stringPreferencesKey("icon_size")

    val settingsFlow: Flow<ThemeSettings> = context.coreDataStore.data.map { prefs ->
        ThemeSettings(
            palette = themePaletteFromStorage(prefs[paletteKey]),
            textSize = textSizeFromStorage(prefs[textSizeKey]),
            iconSize = iconSizeFromStorage(prefs[iconSizeKey]),
        )
    }

    suspend fun setPalette(palette: ThemePalette) {
        context.coreDataStore.edit { prefs ->
            prefs[paletteKey] = palette.storageValue
        }
    }

    suspend fun setTextSize(textSize: TextSize) {
        context.coreDataStore.edit { prefs ->
            prefs[textSizeKey] = textSize.storageValue
        }
    }

    suspend fun setIconSize(iconSize: IconSize) {
        context.coreDataStore.edit { prefs ->
            prefs[iconSizeKey] = iconSize.storageValue
        }
    }

    suspend fun resetToDefault() {
        context.coreDataStore.edit { prefs ->
            prefs[paletteKey] = ThemePalette.System.storageValue
            prefs[textSizeKey] = TextSize.Normal.storageValue
            prefs[iconSizeKey] = IconSize.Normal.storageValue
        }
    }
}
