package com.easyui.core.home

data class AppComponentRef(
    val packageName: String,
    val activityName: String,
)

fun AppComponentRef.toStorageString(): String = "$packageName|$activityName"

fun appComponentRefFromStorageString(raw: String?): AppComponentRef? {
    val value = raw?.trim().orEmpty()
    if (value.isEmpty()) return null
    val parts = value.split("|", limit = 2)
    if (parts.size != 2) return null
    val pkg = parts[0].trim()
    val act = parts[1].trim()
    if (pkg.isEmpty() || act.isEmpty()) return null
    return AppComponentRef(pkg, act)
}

