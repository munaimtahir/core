package com.easyui.core.theme

enum class ThemePalette(val storageValue: String) {
    System("system"),
    Light("light"),
    Dark("dark"),
}

fun themePaletteFromStorage(raw: String?): ThemePalette {
    val value = raw?.trim().orEmpty()
    return when (value) {
        "high_contrast" -> ThemePalette.Dark
        else -> ThemePalette.entries.firstOrNull { it.storageValue == value } ?: ThemePalette.System
    }
}
