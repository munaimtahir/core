package com.easyui.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.easyui.core.theme.ThemeId

private val CoreColorScheme = darkColorScheme(
    background = CoreDarkBackground,
    surface = CoreDarkSurface,
    onSurface = CoreDarkOnSurface,
    onSurfaceVariant = CoreDarkOnSurfaceVariant,
)

@Composable
fun CoreTheme(
    themeId: ThemeId = ThemeId.Default,
    content: @Composable () -> Unit,
) {
    val colors = when (themeId) {
        ThemeId.Default -> CoreColorScheme
        ThemeId.HighContrast -> darkColorScheme(
            background = androidx.compose.ui.graphics.Color.Black,
            surface = androidx.compose.ui.graphics.Color.Black,
            onSurface = androidx.compose.ui.graphics.Color.White,
            onSurfaceVariant = androidx.compose.ui.graphics.Color.White,
            primary = androidx.compose.ui.graphics.Color.White,
        )
        ThemeId.LargeText -> CoreColorScheme
    }

    val typography = when (themeId) {
        ThemeId.LargeText -> scaleTypography(MaterialTheme.typography, 1.2f)
        else -> MaterialTheme.typography
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content,
    )
}

private fun scaleTypography(base: Typography, factor: Float): Typography {
    fun scale(unit: TextUnit): TextUnit {
        // Compose defines 'Unspecified' as NaN; preserve it.
        if (unit == TextUnit.Unspecified) return unit
        return (unit.value * factor).sp
    }

    return base.copy(
        displayLarge = base.displayLarge.copy(fontSize = scale(base.displayLarge.fontSize)),
        displayMedium = base.displayMedium.copy(fontSize = scale(base.displayMedium.fontSize)),
        displaySmall = base.displaySmall.copy(fontSize = scale(base.displaySmall.fontSize)),
        headlineLarge = base.headlineLarge.copy(fontSize = scale(base.headlineLarge.fontSize)),
        headlineMedium = base.headlineMedium.copy(fontSize = scale(base.headlineMedium.fontSize)),
        headlineSmall = base.headlineSmall.copy(fontSize = scale(base.headlineSmall.fontSize)),
        titleLarge = base.titleLarge.copy(fontSize = scale(base.titleLarge.fontSize)),
        titleMedium = base.titleMedium.copy(fontSize = scale(base.titleMedium.fontSize)),
        titleSmall = base.titleSmall.copy(fontSize = scale(base.titleSmall.fontSize)),
        bodyLarge = base.bodyLarge.copy(fontSize = scale(base.bodyLarge.fontSize)),
        bodyMedium = base.bodyMedium.copy(fontSize = scale(base.bodyMedium.fontSize)),
        bodySmall = base.bodySmall.copy(fontSize = scale(base.bodySmall.fontSize)),
        labelLarge = base.labelLarge.copy(fontSize = scale(base.labelLarge.fontSize)),
        labelMedium = base.labelMedium.copy(fontSize = scale(base.labelMedium.fontSize)),
        labelSmall = base.labelSmall.copy(fontSize = scale(base.labelSmall.fontSize)),
    )
}
