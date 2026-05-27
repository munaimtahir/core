package com.easyui.core.home

data class HomeGridSpec(
    val pageCount: Int = 3,
    val columns: Int = 3,
    val rows: Int = 3,
) {
    init {
        require(pageCount >= 1) { "pageCount must be at least 1" }
        require(columns >= 1) { "columns must be at least 1" }
        require(rows >= 1) { "rows must be at least 1" }
    }

    val slotsPerPage: Int = columns * rows
}
