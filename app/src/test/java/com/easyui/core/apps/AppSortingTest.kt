package com.easyui.core.apps

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class AppSortingTest {

    @Test
    fun safeLabel_usesFallbackWhenBlank() {
        assertEquals("fallback", safeLabel("   ", fallback = "fallback"))
        assertEquals("fallback", safeLabel(null, fallback = "fallback"))
    }

    @Test
    fun sortAppsByLabel_sortsAlphabetically() {
        val apps = listOf(
            LaunchableApp("Zeta", "p.z", "A"),
            LaunchableApp("Alpha", "p.a", "A"),
            LaunchableApp("Beta", "p.b", "A"),
        )

        val sorted = sortAppsByLabel(apps, locale = Locale.US).map { it.label }
        assertEquals(listOf("Alpha", "Beta", "Zeta"), sorted)
    }
}
