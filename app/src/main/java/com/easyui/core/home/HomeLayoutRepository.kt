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
    private val spec: HomeGridSpec = HomeGridSpec(),
) {
    private val pageCountKey = intPreferencesKey("home_page_count")

    private fun keyFor(slot: HomeSlotId): Preferences.Key<String> =
        stringPreferencesKey("home_slot_${slot.pageIndex}_${slot.slotIndex}")

    val layoutFlow: Flow<HomeLayout> = context.coreDataStore.data.map { prefs ->
        val storedPageCount = prefs[pageCountKey]?.coerceIn(1, 5) ?: spec.pageCount
        val currentSpec = spec.copy(pageCount = storedPageCount)
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
        val clamped = pageCount.coerceIn(1, 5)
        context.coreDataStore.edit { prefs ->
            prefs[pageCountKey] = clamped
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
        val pageCount = currentPageCount()
        context.coreDataStore.edit { prefs ->
            for (p in 0 until pageCount) {
                for (s in 0 until spec.slotsPerPage) {
                    prefs.remove(keyFor(HomeSlotId(p, s)))
                }
            }
        }
    }

    private suspend fun currentPageCount(): Int {
        return context.coreDataStore.data.map { prefs ->
            prefs[pageCountKey]?.coerceIn(1, 5) ?: spec.pageCount
        }.first()
    }
}
