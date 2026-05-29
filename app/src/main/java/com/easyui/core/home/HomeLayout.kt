package com.easyui.core.home

data class HomeLayout(
    val spec: HomeGridSpec = HomeGridSpec(),
    val pages: List<List<HomeTileContent?>> = List(HomeGridSpec().pageCount) {
        List(HomeGridSpec().slotsPerPage) { null }
    },
) {
    init {
        require(pages.size == spec.pageCount) { "pages.size must equal spec.pageCount" }
        require(pages.all { it.size == spec.slotsPerPage }) { "each page must have spec.slotsPerPage slots" }
    }

    fun get(slot: HomeSlotId): HomeTileContent? = pages[slot.pageIndex][slot.slotIndex]

    fun assign(slot: HomeSlotId, content: HomeTileContent?): HomeLayout {
        requireValid(slot)
        val newPages = pages.mapIndexed { p, page ->
            if (p != slot.pageIndex) page
            else page.mapIndexed { i, current -> if (i == slot.slotIndex) content else current }
        }
        return copy(pages = newPages)
    }

    fun move(from: HomeSlotId, to: HomeSlotId): HomeLayout {
        requireValid(from)
        requireValid(to)
        if (from == to) return this
        val fromContent = get(from)
        val toContent = get(to)
        return assign(from, toContent).assign(to, fromContent)
    }

    fun clearAll(): HomeLayout = copy(
        pages = List(spec.pageCount) { List(spec.slotsPerPage) { null } },
    )

    fun filledSlotsCount(): Int = pages.sumOf { page -> page.count { it != null } }

    fun firstEmptySlot(): HomeSlotId? {
        for (p in 0 until spec.pageCount) {
            for (s in 0 until spec.slotsPerPage) {
                if (pages[p][s] == null) return HomeSlotId(p, s)
            }
        }
        return null
    }

    private fun requireValid(slot: HomeSlotId) {
        require(slot.pageIndex in 0 until spec.pageCount) { "pageIndex out of bounds" }
        require(slot.slotIndex in 0 until spec.slotsPerPage) { "slotIndex out of bounds" }
    }
}

