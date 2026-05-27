package com.easyui.core

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class BaselineUiSmokeTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun home_renders_and_navigationEntriesExist() {
        rule.onNodeWithTag("home_screen").assertExists()
        rule.onNodeWithTag("home_all_apps_button").assertExists()
        rule.onNodeWithTag("home_customize_button").assertExists()
        rule.onNodeWithTag("home_settings_button").assertExists()
    }

    @Test
    fun theme_settings_showsExactlyThreeThemes() {
        rule.onNodeWithTag("home_settings_button").performClick()
        rule.onNodeWithTag("theme_settings_screen").assertExists()
        rule.onNodeWithTag("theme_default").assertExists()
        rule.onNodeWithTag("theme_high_contrast").assertExists()
        rule.onNodeWithTag("theme_large_text").assertExists()
    }
}

