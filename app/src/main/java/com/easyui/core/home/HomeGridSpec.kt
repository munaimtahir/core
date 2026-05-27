package com.easyui.core.home

data class HomeGridSpec(
    val pageCount: Int = 3,
    val columns: Int = 3,
    val rows: Int = 3,
) {
    val slotsPerPage: Int = columns * rows
}

