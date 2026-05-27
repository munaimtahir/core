package com.easyui.core.theme

enum class IconSize(val storageValue: String) {
    Normal("normal"),
    Large("large"),
}

fun iconSizeFromStorage(raw: String?): IconSize {
    val value = raw?.trim().orEmpty()
    return IconSize.entries.firstOrNull { it.storageValue == value } ?: IconSize.Normal
}

