package com.easyui.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CoreColorScheme = darkColorScheme(
    background = CoreDarkBackground,
    surface = CoreDarkSurface,
    onSurface = CoreDarkOnSurface,
    onSurfaceVariant = CoreDarkOnSurfaceVariant,
)

@Composable
fun CoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CoreColorScheme,
        content = content,
    )
}

