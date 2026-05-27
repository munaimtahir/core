package com.easyui.core.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeIdTest {
    @Test
    fun themeIdFromStorage_defaultsSafely() {
        assertEquals(ThemeId.Default, themeIdFromStorage(null))
        assertEquals(ThemeId.Default, themeIdFromStorage(""))
        assertEquals(ThemeId.Default, themeIdFromStorage("unknown"))
    }

    @Test
    fun themeIdFromStorage_parsesKnownValues() {
        assertEquals(ThemeId.Default, themeIdFromStorage("default"))
        assertEquals(ThemeId.HighContrast, themeIdFromStorage("high_contrast"))
        assertEquals(ThemeId.LargeText, themeIdFromStorage("large_text"))
    }
}

