package com.easyui.core.theme

enum class ThemeId(val storageValue: String) {
    Default("default"),
    HighContrast("high_contrast"),
    LargeText("large_text"),
}

fun themeIdFromStorage(raw: String?): ThemeId {
    val value = raw?.trim().orEmpty()
    return ThemeId.entries.firstOrNull { it.storageValue == value } ?: ThemeId.Default
}

