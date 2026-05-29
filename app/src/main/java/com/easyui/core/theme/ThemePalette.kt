package com.easyui.core.theme

enum class ThemePalette(val storageValue: String) {
    System("system"),
    Light("light"),
    Dark("dark"),
    HighContrast("high_contrast")
}

fun themePaletteFromStorage(raw: String?): ThemePalette {
    val value = raw?.trim().orEmpty()
    return ThemePalette.entries.firstOrNull { it.storageValue == value } ?: ThemePalette.System
}
