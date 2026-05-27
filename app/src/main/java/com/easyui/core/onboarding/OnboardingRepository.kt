package com.easyui.core.onboarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.easyui.core.storage.coreDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OnboardingRepository(
    private val context: Context,
) {
    private val completedKey = booleanPreferencesKey("onboarding_completed")

    val isCompletedFlow: Flow<Boolean> = context.coreDataStore.data.map { prefs ->
        prefs[completedKey] ?: false
    }

    suspend fun markCompleted() {
        context.coreDataStore.edit { prefs ->
            prefs[completedKey] = true
        }
    }

    suspend fun reset() {
        context.coreDataStore.edit { prefs ->
            prefs[completedKey] = false
        }
    }
}

