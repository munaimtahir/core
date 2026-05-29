package com.easyui.core.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
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
    private val showLabelsKey = booleanPreferencesKey("show_labels")
    private val accentKey = stringPreferencesKey("theme_accent")
    private val tileShapeKey = stringPreferencesKey("theme_tile_shape")
    private val backgroundKey = stringPreferencesKey("theme_background")
    private val reducedMotionKey = booleanPreferencesKey("reduced_motion")

    val settingsFlow: Flow<ThemeSettings> = context.coreDataStore.data.map { prefs ->
        ThemeSettings(
            palette = themePaletteFromStorage(prefs[paletteKey]),
            textSize = textSizeFromStorage(prefs[textSizeKey]),
            iconSize = iconSizeFromStorage(prefs[iconSizeKey]),
            showLabels = prefs[showLabelsKey] ?: true,
            accent = ThemeAccent.fromStorage(prefs[accentKey]),
            tileShape = ThemeTileShape.fromStorage(prefs[tileShapeKey]),
            background = ThemeBackground.fromStorage(prefs[backgroundKey]),
            reducedMotion = prefs[reducedMotionKey] ?: false,
        )
    }

    suspend fun setPalette(palette: ThemePalette) {
        context.coreDataStore.edit { prefs -> prefs[paletteKey] = palette.storageValue }
    }

    suspend fun setTextSize(textSize: TextSize) {
        context.coreDataStore.edit { prefs -> prefs[textSizeKey] = textSize.storageValue }
    }

    suspend fun setIconSize(iconSize: IconSize) {
        context.coreDataStore.edit { prefs -> prefs[iconSizeKey] = iconSize.storageValue }
    }
    
    suspend fun setShowLabels(show: Boolean) {
        context.coreDataStore.edit { prefs -> prefs[showLabelsKey] = show }
    }

    suspend fun setAccent(accent: ThemeAccent) {
        context.coreDataStore.edit { prefs -> prefs[accentKey] = accent.storageValue }
    }

    suspend fun setTileShape(shape: ThemeTileShape) {
        context.coreDataStore.edit { prefs -> prefs[tileShapeKey] = shape.storageValue }
    }

    suspend fun setBackground(background: ThemeBackground) {
        context.coreDataStore.edit { prefs -> prefs[backgroundKey] = background.storageValue }
    }

    suspend fun setReducedMotion(reduced: Boolean) {
        context.coreDataStore.edit { prefs -> prefs[reducedMotionKey] = reduced }
    }

    suspend fun resetToDefault() {
        context.coreDataStore.edit { prefs ->
            prefs[paletteKey] = ThemePalette.System.storageValue
            prefs[textSizeKey] = TextSize.Normal.storageValue
            prefs[iconSizeKey] = IconSize.Normal.storageValue
            prefs[showLabelsKey] = true
            prefs[accentKey] = ThemeAccent.Default.storageValue
            prefs[tileShapeKey] = ThemeTileShape.SoftRounded.storageValue
            prefs[backgroundKey] = ThemeBackground.Default.storageValue
            prefs[reducedMotionKey] = false
        }
    }
}
