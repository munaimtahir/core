package com.easyui.core.theme

data class ThemeSettings(
    val palette: ThemePalette = ThemePalette.System,
    val textSize: TextSize = TextSize.Normal,
    val iconSize: IconSize = IconSize.Normal,
    val showLabels: Boolean = true,
    val accent: ThemeAccent = ThemeAccent.Default,
    val tileShape: ThemeTileShape = ThemeTileShape.SoftRounded,
    val background: ThemeBackground = ThemeBackground.Default,
    val reducedMotion: Boolean = false,
)

