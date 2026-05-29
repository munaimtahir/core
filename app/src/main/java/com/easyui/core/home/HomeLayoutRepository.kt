package com.easyui.core.home

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.easyui.core.storage.coreDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class HomeLayoutRepository(
    private val context: Context,
    private val fallbackSpec: HomeGridSpec = HomeGridSpec(),
) {
    private val pageCountKey = intPreferencesKey("home_page_count")
    private val gridColumnsKey = intPreferencesKey("home_grid_columns")
    private val gridRowsKey = intPreferencesKey("home_grid_rows")

    private fun keyFor(slot: HomeSlotId): Preferences.Key<String> =
        stringPreferencesKey("home_slot_${slot.pageIndex}_${slot.slotIndex}")

    val layoutFlow: Flow<HomeLayout> = context.coreDataStore.data.map { prefs ->
        val storedPageCount = prefs[pageCountKey]?.coerceIn(1, 9) ?: fallbackSpec.pageCount
        val storedColumns = prefs[gridColumnsKey] ?: fallbackSpec.columns
        val storedRows = prefs[gridRowsKey] ?: fallbackSpec.rows
        
        // Ensure grid size is valid (2x2, 3x3, 4x4)
        val validColumns = if (storedColumns in listOf(2, 3, 4)) storedColumns else 3
        val validRows = if (storedRows in listOf(2, 3, 4)) storedRows else 3

        val currentSpec = fallbackSpec.copy(
            pageCount = storedPageCount,
            columns = validColumns,
            rows = validRows
        )

        val pages = List(currentSpec.pageCount) { pageIndex ->
            List(currentSpec.slotsPerPage) { slotIndex ->
                val raw = prefs[keyFor(HomeSlotId(pageIndex, slotIndex))]
                appComponentRefFromStorageString(raw)
            }
        }
        HomeLayout(spec = currentSpec, pages = pages)
    }

    suspend fun setSlot(slot: HomeSlotId, ref: AppComponentRef?) {
        context.coreDataStore.edit { prefs ->
            val key = keyFor(slot)
            if (ref == null) prefs.remove(key) else prefs[key] = ref.toStorageString()
        }
    }

    suspend fun setPageCount(pageCount: Int) {
        val clamped = pageCount.coerceIn(1, 9)
        context.coreDataStore.edit { prefs ->
            prefs[pageCountKey] = clamped
        }
    }
    
    suspend fun setGridSize(columns: Int, rows: Int) {
        val validColumns = if (columns in listOf(2, 3, 4)) columns else 3
        val validRows = if (rows in listOf(2, 3, 4)) rows else 3
        context.coreDataStore.edit { prefs ->
            prefs[gridColumnsKey] = validColumns
            prefs[gridRowsKey] = validRows
        }
    }

    suspend fun increasePageCount() {
        val current = currentPageCount()
        setPageCount(current + 1)
    }

    suspend fun decreasePageCount() {
        val current = currentPageCount()
        setPageCount(current - 1)
    }

    suspend fun resetHome() {
        context.coreDataStore.edit { prefs ->
            val keysToRemove = prefs.asMap().keys.filter { it.name.startsWith("home_slot_") }
            keysToRemove.forEach { prefs.remove(it) }
        }
    }

    private suspend fun currentPageCount(): Int {
        return context.coreDataStore.data.map { prefs ->
            prefs[pageCountKey]?.coerceIn(1, 9) ?: fallbackSpec.pageCount
        }.first()
    }
}
