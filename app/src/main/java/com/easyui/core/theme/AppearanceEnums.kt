package com.easyui.core.theme

enum class ThemeAccent(val storageValue: String) {
    Default("default"),
    Blue("blue"),
    Green("green"),
    Purple("purple"),
    Orange("orange"),
    Red("red");

    companion object {
        fun fromStorage(raw: String?): ThemeAccent = entries.firstOrNull { it.storageValue == raw } ?: Default
    }
}

enum class ThemeTileShape(val storageValue: String) {
    SoftRounded("soft_rounded"),
    ExtraRounded("extra_rounded"),
    Square("square");

    companion object {
        fun fromStorage(raw: String?): ThemeTileShape = entries.firstOrNull { it.storageValue == raw } ?: SoftRounded
    }
}

enum class ThemeBackground(val storageValue: String) {
    Default("default"),
    SolidDark("solid_dark"),
    SolidLight("solid_light"),
    PitchBlack("pitch_black");

    companion object {
        fun fromStorage(raw: String?): ThemeBackground = entries.firstOrNull { it.storageValue == raw } ?: Default
    }
}
