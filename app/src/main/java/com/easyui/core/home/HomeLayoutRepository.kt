package com.easyui.core.home

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.easyui.core.storage.coreDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeLayoutRepository(
    private val context: Context,
    private val spec: HomeGridSpec = HomeGridSpec(),
) {
    private fun keyFor(slot: HomeSlotId): Preferences.Key<String> =
        stringPreferencesKey("home_slot_${slot.pageIndex}_${slot.slotIndex}")

    val layoutFlow: Flow<HomeLayout> = context.coreDataStore.data.map { prefs ->
        val pages = List(spec.pageCount) { pageIndex ->
            List(spec.slotsPerPage) { slotIndex ->
                val raw = prefs[keyFor(HomeSlotId(pageIndex, slotIndex))]
                appComponentRefFromStorageString(raw)
            }
        }
        HomeLayout(spec = spec, pages = pages)
    }

    suspend fun setSlot(slot: HomeSlotId, ref: AppComponentRef?) {
        context.coreDataStore.edit { prefs ->
            val key = keyFor(slot)
            if (ref == null) prefs.remove(key) else prefs[key] = ref.toStorageString()
        }
    }

    suspend fun resetHome() {
        context.coreDataStore.edit { prefs ->
            for (p in 0 until spec.pageCount) {
                for (s in 0 until spec.slotsPerPage) {
                    prefs.remove(keyFor(HomeSlotId(p, s)))
                }
            }
        }
    }
}

