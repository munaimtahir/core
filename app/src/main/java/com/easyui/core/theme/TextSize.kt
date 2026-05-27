package com.easyui.core.theme

enum class TextSize(val storageValue: String, val scaleFactor: Float) {
    Small("small", 0.90f),
    Normal("normal", 1.00f),
    Large("large", 1.18f),
    Larger("larger", 1.32f),
}

fun textSizeFromStorage(raw: String?): TextSize {
    val value = raw?.trim().orEmpty()
    return TextSize.entries.firstOrNull { it.storageValue == value } ?: TextSize.Normal
}
