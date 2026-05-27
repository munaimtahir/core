package com.easyui.core.apps

sealed interface LaunchResult {
    data object Success : LaunchResult
    data class Failure(val message: String) : LaunchResult
}

interface AppLauncher {
    fun launch(app: LaunchableApp): LaunchResult
}

