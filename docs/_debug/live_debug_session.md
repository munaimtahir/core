# Live Debug Session

## Session purpose

Fast one-bug-at-a-time debugging while the user manually tests the app on a real device/emulator.

## Rules

- no PR per bug
- no commit per bug
- no push per bug
- no broad refactor per bug
- one bug at a time
- minimal fix
- focused check
- user verifies
- batch verification after bug group

## Bug ledger

| Bug ID | User symptom | Area | Files changed | Check run | Status |
|---|---|---|---|---|---|
| BUG-001 | All Apps list shows only some installed apps | App discovery / Package visibility | `app/src/main/AndroidManifest.xml`, `app/src/main/res/mipmap-anydpi-v26/ic_launcher*.xml` | `./gradlew assembleDebug` | pending user verification |
| BUG-002 | All Apps list scroll only works when dragging on the app name; dragging on the left/icon/empty space doesn't scroll | All Apps UI / touch handling | `app/src/main/java/com/easyui/core/MainActivity.kt` | `./gradlew assembleDebug` | pending user verification |
| BUG-003 | Home pages only change via Previous/Next buttons; swipe/slide does not change pages | Home UI / paging gesture | `app/src/main/java/com/easyui/core/MainActivity.kt` | `./gradlew assembleDebug` | pending user verification |
| BUG-004 | Back button on Home exits to a blank wallpaper screen instead of staying on Home | Navigation / back handling | `app/src/main/java/com/easyui/core/MainActivity.kt` | `./gradlew assembleDebug` | pending user verification |
| BUG-005 | After choosing Core as the default Home app, onboarding never completes and the launcher loops back to onboarding instead of Home | Onboarding / default launcher completion | `app/src/main/java/com/easyui/core/MainActivity.kt` | `./gradlew assembleDebug`, `./gradlew testDebugUnitTest`, `./gradlew lintDebug` | verified by user |
| BUG-006 | Theme settings need clearer text-size selection, scale context, and no separate High Contrast option if it matches Dark | Theme settings / palette and typography controls | `app/src/main/java/com/easyui/core/MainActivity.kt`, `app/src/main/java/com/easyui/core/theme/ThemePalette.kt`, `app/src/main/java/com/easyui/core/theme/TextSize.kt`, `app/src/main/java/com/easyui/core/ui/theme/Theme.kt`, `app/src/main/res/values/strings.xml`, `app/src/test/java/com/easyui/core/theme/ThemeSettingsParsingTest.kt`, `app/src/androidTest/java/com/easyui/core/BaselineUiSmokeTest.kt` | `./gradlew clean assembleDebug`, `./gradlew testDebugUnitTest`, `./gradlew lintDebug` | pending user verification |
