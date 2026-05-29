package com.easyui.core.apps

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.easyui.core.storage.coreDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppDrawerRepository(private val context: Context) {
    private val isGridModeKey = booleanPreferencesKey("app_drawer_is_grid_mode")
    private val favoritesKey = stringSetPreferencesKey("app_drawer_favorites")

    val isGridModeFlow: Flow<Boolean> = context.coreDataStore.data.map { prefs ->
        prefs[isGridModeKey] ?: false
    }

    val favoritesFlow: Flow<Set<String>> = context.coreDataStore.data.map { prefs ->
        prefs[favoritesKey] ?: emptySet()
    }

    suspend fun setGridMode(isGrid: Boolean) {
        context.coreDataStore.edit { prefs ->
            prefs[isGridModeKey] = isGrid
        }
    }

    suspend fun toggleFavorite(packageName: String, activityName: String) {
        val id = "$packageName|$activityName"
        context.coreDataStore.edit { prefs ->
            val current = prefs[favoritesKey]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(id)) {
                current.remove(id)
            } else {
                current.add(id)
            }
            prefs[favoritesKey] = current
        }
    }
}
