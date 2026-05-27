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
| BUG-007 | After initial setup, there is no obvious way to reopen onboarding / launcher setup from Home | Home screen / setup entry point | `app/src/main/java/com/easyui/core/MainActivity.kt`, `app/src/main/res/values/strings.xml` | `./gradlew assembleDebug` | pending user verification |
| BUG-008 | Theme screen is not vertically scrollable, so text-size options below the fold are hard to reach | Theme settings / scrolling | `app/src/main/java/com/easyui/core/MainActivity.kt`, `app/src/main/res/values/strings.xml` | `./gradlew assembleDebug` | verified locally |
| BUG-009 | Need to adjust home page count from the UI and keep empty slots hidden on Home unless editing | Home grid / page count / edit-mode rendering | `app/src/main/java/com/easyui/core/MainActivity.kt`, `app/src/main/res/values/strings.xml` | `./gradlew assembleDebug` | ready for user verification |
| BUG-010 | Split onboarding into launcher, appearance, and layout steps; remove setup shortcuts from Home; persist home page count | Onboarding / Home setup entry points / home layout storage | `app/src/main/java/com/easyui/core/MainActivity.kt`, `app/src/main/java/com/easyui/core/home/HomeLayoutRepository.kt`, `app/src/main/java/com/easyui/core/home/HomeGridSpec.kt`, `app/src/main/res/values/strings.xml`, `copilot_session.md` | `./gradlew assembleDebug`, `./gradlew testDebugUnitTest`, `./gradlew lintDebug` | locally verified |
| BUG-011 | Home needs a full-width top clock strip showing time, date, and month/day | Home screen / clock header | `app/src/main/java/com/easyui/core/MainActivity.kt`, `copilot_session.md` | `./gradlew assembleDebug` | locally verified |
| BUG-012 | Replace Home title/subtitle bar with a secondary status strip showing SIM/network, connection type, network name, and battery percentage | Home screen / status header | `app/src/main/java/com/easyui/core/MainActivity.kt`, `app/src/main/AndroidManifest.xml`, `copilot_session.md` | `./gradlew assembleDebug`, `./gradlew testDebugUnitTest`, `./gradlew lintDebug`, `adb install -r app/build/outputs/apk/debug/app-debug.apk` | locally verified |
