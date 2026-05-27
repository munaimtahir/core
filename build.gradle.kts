// Root build file for the `core` Android launcher foundation.
//
// Intentionally minimal: module-specific configuration lives in each module.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
