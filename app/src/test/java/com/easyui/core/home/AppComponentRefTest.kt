package com.easyui.core.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppComponentRefTest {
    @Test
    fun storageRoundTrip() {
        val ref = AppComponentRef("pkg", "act")
        val raw = ref.toStorageString()
        assertEquals(ref, appComponentRefFromStorageString(raw))
    }

    @Test
    fun parse_invalid_returnsNull() {
        assertNull(appComponentRefFromStorageString(null))
        assertNull(appComponentRefFromStorageString(""))
        assertNull(appComponentRefFromStorageString("pkg-only"))
        assertNull(appComponentRefFromStorageString(" | "))
        assertNull(appComponentRefFromStorageString("pkg|"))
        assertNull(appComponentRefFromStorageString("|act"))
    }
}

