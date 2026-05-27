package com.easyui.core.apps

import java.text.Collator
import java.util.Locale

fun sortAppsByLabel(apps: List<LaunchableApp>, locale: Locale = Locale.getDefault()): List<LaunchableApp> {
    val collator = Collator.getInstance(locale)
    return apps.sortedWith { a, b -> collator.compare(a.label, b.label) }
}

fun safeLabel(label: CharSequence?, fallback: String): String {
    val text = label?.toString()?.trim().orEmpty()
    return if (text.isNotEmpty()) text else fallback
}

