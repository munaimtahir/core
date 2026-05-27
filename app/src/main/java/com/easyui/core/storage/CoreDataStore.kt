package com.easyui.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val DATASTORE_NAME = "core_preferences"

val Context.coreDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME,
)

