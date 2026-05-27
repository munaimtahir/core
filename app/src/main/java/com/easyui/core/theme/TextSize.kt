package com.easyui.core.theme

enum class TextSize(val storageValue: String) {
    Small("small"),
    Normal("normal"),
    Large("large"),
    Larger("larger"),
}

fun textSizeFromStorage(raw: String?): TextSize {
    val value = raw?.trim().orEmpty()
    return TextSize.entries.firstOrNull { it.storageValue == value } ?: TextSize.Normal
}
