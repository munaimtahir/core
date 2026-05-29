package com.easyui.core.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeSettingsParsingTest {

    @Test
    fun themePaletteFromStorage_defaultsSafely() {
        assertEquals(ThemePalette.System, themePaletteFromStorage(null))
        assertEquals(ThemePalette.System, themePaletteFromStorage(""))
        assertEquals(ThemePalette.System, themePaletteFromStorage("unknown"))
    }

    @Test
    fun themePaletteFromStorage_parsesKnownValues() {
        assertEquals(ThemePalette.System, themePaletteFromStorage("system"))
        assertEquals(ThemePalette.Light, themePaletteFromStorage("light"))
        assertEquals(ThemePalette.Dark, themePaletteFromStorage("dark"))
        assertEquals(ThemePalette.HighContrast, themePaletteFromStorage("high_contrast"))
    }

    @Test
    fun textSizeFromStorage_defaultsSafely() {
        assertEquals(TextSize.Normal, textSizeFromStorage(null))
        assertEquals(TextSize.Normal, textSizeFromStorage(""))
        assertEquals(TextSize.Normal, textSizeFromStorage("unknown"))
        assertEquals(TextSize.Small, textSizeFromStorage("small"))
        assertEquals(TextSize.Large, textSizeFromStorage("large"))
        assertEquals(TextSize.Larger, textSizeFromStorage("larger"))
    }

    @Test
    fun iconSizeFromStorage_defaultsSafely() {
        assertEquals(IconSize.Normal, iconSizeFromStorage(null))
        assertEquals(IconSize.Normal, iconSizeFromStorage(""))
        assertEquals(IconSize.Normal, iconSizeFromStorage("unknown"))
        assertEquals(IconSize.Large, iconSizeFromStorage("large"))
    }
}
