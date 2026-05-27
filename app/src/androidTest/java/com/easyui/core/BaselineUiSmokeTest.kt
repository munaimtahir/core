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
    fun theme_settings_showsThemeOptions() {
        rule.onNodeWithTag("home_settings_button").performClick()
        rule.onNodeWithTag("theme_settings_screen").assertExists()
        rule.onNodeWithTag("theme_palette_system").assertExists()
        rule.onNodeWithTag("theme_palette_light").assertExists()
        rule.onNodeWithTag("theme_palette_dark").assertExists()
        rule.onNodeWithTag("theme_palette_high_contrast").assertExists()
        rule.onNodeWithTag("theme_text_normal").assertExists()
        rule.onNodeWithTag("theme_text_large").assertExists()
        rule.onNodeWithTag("theme_icons_normal").assertExists()
        rule.onNodeWithTag("theme_icons_large").assertExists()
    }
}
