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
    private val themeKey = stringPreferencesKey("theme_id")

    val themeFlow: Flow<ThemeId> = context.coreDataStore.data.map { prefs ->
        themeIdFromStorage(prefs[themeKey])
    }

    suspend fun setTheme(themeId: ThemeId) {
        context.coreDataStore.edit { prefs ->
            prefs[themeKey] = themeId.storageValue
        }
    }

    suspend fun resetToDefault() {
        setTheme(ThemeId.Default)
    }
}

