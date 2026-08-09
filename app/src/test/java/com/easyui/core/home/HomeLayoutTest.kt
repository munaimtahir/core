package com.easyui.core.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeLayoutTest {
    private val spec = HomeGridSpec(pageCount = 3, columns = 3, rows = 3)

    private fun app(packageName: String, activityName: String = "MainActivity"): HomeTileContent =
        HomeTileContent.App(AppComponentRef(packageName, activityName))

    @Test
    fun assign_setsAndClearsSlot() {
        val layout = HomeLayout(spec = spec)
        val slot = HomeSlotId(pageIndex = 0, slotIndex = 0)
        val content = app("pkg", "act")

        val assigned = layout.assign(slot, content)
        assertEquals(content, assigned.get(slot))

        val cleared = assigned.assign(slot, null)
        assertEquals(null, cleared.get(slot))
    }

    @Test
    fun move_swapsSlotsAcrossPages() {
        val a = app("a", "A")
        val b = app("b", "B")
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
            .assign(HomeSlotId(0, 0), app("a", "A"))
            .assign(HomeSlotId(1, 3), app("b", "B"))

        val cleared = layout.clearAll()
        assertEquals(0, cleared.filledSlotsCount())
    }

    @Test
    fun supportedTileTypes_areAssignedAndRetainIdentity() {
        val contact = HomeTileContent.Contact("Alice", "123", ContactAction.Dial)
        val widget = HomeTileContent.Widget(LocalWidgetType.Note)
        val layout = HomeLayout(spec = HomeGridSpec(pageCount = 1, columns = 2, rows = 2))
            .assign(HomeSlotId(0, 0), contact)
            .assign(HomeSlotId(0, 1), widget)

        assertEquals(contact, layout.get(HomeSlotId(0, 0)))
        assertEquals(widget, layout.get(HomeSlotId(0, 1)))
        assertEquals(HomeSlotId(0, 2), layout.firstEmptySlot())
    }

    @Test
    fun gridSpec_controlsSlotBounds_for2x2And4x4() {
        val compact = HomeLayout(spec = HomeGridSpec(pageCount = 1, columns = 2, rows = 2))
        val expanded = HomeLayout(spec = HomeGridSpec(pageCount = 1, columns = 4, rows = 4))

        assertEquals(4, compact.spec.slotsPerPage)
        assertEquals(16, expanded.spec.slotsPerPage)
        assertEquals(HomeSlotId(0, 0), compact.firstEmptySlot())
        assertEquals(HomeSlotId(0, 0), expanded.firstEmptySlot())

        val compactFilled = (0 until compact.spec.slotsPerPage).fold(compact) { current, slot ->
            current.assign(HomeSlotId(0, slot), app("compact.$slot"))
        }
        val expandedFilled = (0 until expanded.spec.slotsPerPage).fold(expanded) { current, slot ->
            current.assign(HomeSlotId(0, slot), app("expanded.$slot"))
        }
        assertEquals(null, compactFilled.firstEmptySlot())
        assertEquals(null, expandedFilled.firstEmptySlot())
    }

    @Test(expected = IllegalArgumentException::class)
    fun assign_rejectsSlotOutsideSelectedGrid() {
        HomeLayout(spec = HomeGridSpec(pageCount = 1, columns = 2, rows = 2))
            .assign(HomeSlotId(0, 4), app("out.of.bounds"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun move_rejectsPageOutsideSelectedGrid() {
        HomeLayout(spec = HomeGridSpec(pageCount = 2, columns = 2, rows = 2))
            .move(HomeSlotId(0, 0), HomeSlotId(2, 0))
    }
}
