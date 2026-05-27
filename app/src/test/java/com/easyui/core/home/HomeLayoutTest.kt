package com.easyui.core.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeLayoutTest {
    private val spec = HomeGridSpec(pageCount = 3, columns = 3, rows = 3)

    @Test
    fun assign_setsAndClearsSlot() {
        val layout = HomeLayout(spec = spec)
        val slot = HomeSlotId(pageIndex = 0, slotIndex = 0)
        val ref = AppComponentRef("pkg", "act")

        val assigned = layout.assign(slot, ref)
        assertEquals(ref, assigned.get(slot))

        val cleared = assigned.assign(slot, null)
        assertEquals(null, cleared.get(slot))
    }

    @Test
    fun move_swapsSlotsAcrossPages() {
        val a = AppComponentRef("a", "A")
        val b = AppComponentRef("b", "B")
        val from = HomeSlotId(0, 0)
        val to = HomeSlotId(2, 5)

        val layout = HomeLayout(spec = spec)
            .assign(from, a)
            .assign(to, b)

        val moved = layout.move(from, to)
        assertEquals(b, moved.get(from))
        assertEquals(a, moved.get(to))
    }

    @Test
    fun clearAll_removesAllApps() {
        val layout = HomeLayout(spec = spec)
            .assign(HomeSlotId(0, 0), AppComponentRef("a", "A"))
            .assign(HomeSlotId(1, 3), AppComponentRef("b", "B"))

        val cleared = layout.clearAll()
        assertEquals(0, cleared.filledSlotsCount())
    }
}

