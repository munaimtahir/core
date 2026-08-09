# Core Launcher Foundation — v0.1 Final Baseline Verification Report

Date: 2026-08-09
Project: `core` / Core Launcher Foundation
Final verdict: **GO**

## 1. Executive summary

The previous NO-GO gates are closed. The project now has a tracked Gradle 8.7 wrapper, passing unit tests, passing lint, passing connected Compose instrumentation tests, a clean APK install/launch, a verified foreground HOME role, and clean runtime evidence on Android 15.

The audit also repaired two regressions discovered during verification: `HomeLayout` now allocates default pages from the selected `HomeGridSpec`, and the connected UI smoke tests now follow the current onboarding and appearance tags. The Home screen now exposes the existing reset flow.

## 2. Repository state

```text
Branch: main
HEAD: d68a65c update
Working tree before this sprint: only the prior copilot_session.md audit update
Working tree after this sprint: implementation, tests, wrapper, documentation, and evidence updates listed below
```

No unrelated legacy code, product-variant logic, cloud state, monetization, or managed-device behavior was added.

## 3. Build environment

```text
Java: OpenJDK 21.0.12
Gradle wrapper: Gradle 8.7
Android Gradle Plugin: 8.5.2
Kotlin: 1.9.24
Compile SDK: 34
Target SDK: 34
```

Runtime target:

```text
AVD: Android_15_Test
Android: 15
Serial: emulator-5554
Resolution: 1080x2340
Density: 440 dpi
```

## 4. Fixes implemented

### Gradle reproducibility

Restored and made executable:

- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`

The wrapper remains pinned to Gradle 8.7 and runs without system Gradle.

### Home layout model and tests

- Migrated `HomeLayoutTest.kt` from `AppComponentRef` values to `HomeTileContent.App`.
- Added coverage for app/contact/widget tile identity, 2x2 and 4x4 specs, empty slots, invalid slot bounds, and invalid page bounds.
- Fixed `HomeLayout` default page construction to use `spec.pageCount` and `spec.slotsPerPage` instead of a hardcoded default 3x3 allocation.

### Connected UI test repair

- Updated onboarding helper from obsolete `onboarding_continue` to the current three-step `onboarding_next` flow.
- Updated obsolete `home_settings_button` assertions to the current `home_appearance_button` tag.
- Added a smoke assertion for the now-visible Reset entry.

### Baseline usability

- Wired the existing Reset Options screen to a visible Home-screen Reset button.
- Removed the always-true unavailable-app null condition.
- Removed the unused onboarding label callback.

## 5. Automated verification

| Command | Result |
|---|---|
| `./gradlew --version` | PASS — Gradle 8.7 |
| `./gradlew tasks` | PASS — project configuration succeeds |
| `./gradlew clean` | PASS |
| `./gradlew assembleDebug` | PASS |
| `./gradlew testDebugUnitTest` | PASS — 16 tests completed |
| `./gradlew lintDebug` | PASS |
| `./gradlew connectedDebugAndroidTest` | PASS — 2 tests on Android 15 |

The final automated sequence was executed after the last source/test changes. The debug build emits only non-blocking deprecation warnings for legacy Wi-Fi APIs and a native-library strip notice for DataStore packaging.

## 6. APK artifact

```text
Path: app/build/outputs/apk/debug/app-debug.apk
Size: 12,234,970 bytes
Package: com.easyui.core
APK type: Android package (APK)
```

ADB package evidence is stored at:

```text
docs/VERIFICATION/artifacts/2026-08-09/pm-path.txt
```

## 7. Runtime verification

### Clean install and launch — PASS

- Previous package state was uninstalled.
- Latest debug APK installed successfully.
- `com.easyui.core/.MainActivity` launched successfully.
- Final foreground activity was `com.easyui.core/.MainActivity`.
- Final screenshot contains the actual Core Launcher home UI with no lock screen, permission dialog, or OEM overlay.
- Launcher-specific logcat scan found no fatal exception or ANR.

### HOME role — PASS

The manifest contains `MAIN` + `HOME` + `DEFAULT`. The emulator was configured with:

```bash
adb shell cmd package set-home-activity com.easyui.core/.MainActivity
adb shell input keyevent 3
```

The HOME resolver then returned:

```text
com.easyui.core/.MainActivity
```

Pressing HOME left Core Launcher in the foreground.

### Clean foreground screenshot — PASS

The final screenshot shows:

- top launcher information strip
- time/date tile
- All apps entry
- page indicator
- Contacts and Widgets entries
- Customize home and Appearance entries
- Quick Access, Status, and Reset entries

## 8. Runtime feature smoke results

| Scenario | Result | Evidence |
|---|---|---|
| Home renders | PASS | `final-launcher-home.png` |
| App drawer opens | PASS | `app-drawer.png`, `app-drawer-ui.xml` |
| Installed labels/icons populate | PASS | App drawer UI dump lists Calendar, Camera, Chrome, Clock, Contacts, Drive, Files, Gmail, Google, and Maps |
| App drawer search | PASS | `app-drawer-search-chrome.png`, `app-drawer-search-ui.xml` show Chrome result |
| Grid/List drawer modes | PASS | `app-drawer-grid.png`, `app-drawer-grid-ui.xml`; List/Grid labels observed |
| App launch and return HOME | PASS | Chrome launched; HOME returned to Core after default HOME setup |
| 2x2 grid | PASS | `grid-2x2-home.png` and UI evidence |
| 4x4 grid | PASS | `grid-4x4-home.png` and UI evidence |
| Grid persistence after process restart | PASS | `grid-4x4-after-process-restart.png`, `grid-4x4-restart-ui.xml` |
| Theme change | PASS | Dark theme selected; `appearance-dark.png` |
| Theme persistence | PASS | `theme-dark-after-restart.png` |
| Quick Access | PASS | `quick-access.png`, `quick-access-ui.xml` |
| Flashlight | PASS | Emulator toggle changed from `Flashlight: OFF` to `Flashlight: ON` without crash |
| Contacts/dial/SMS screen | PASS | `contacts-screen.png`, `contacts-ui.xml`; no calls/messages were sent |
| Built-in widgets | PASS | `widgets-screen.png`; Note tile added and visible in `home-after-note-widget.png` |
| Reset entry and confirmation | PASS | `reset-options-screen.png`, `reset-confirm-dialog.png`, `after-reset-home.png` |
| Launcher crash/ANR scan | PASS | `final-launcher-logcat.txt`, `home-keyevent-logcat.txt` |

The final reset restored the emulator to a safe default theme and cleared the temporary Note tile used during smoke testing.

## 9. Instrumentation test results

```text
./gradlew connectedDebugAndroidTest
PASS
Android_15_Test(AVD) - 15
2 tests completed, 0 failed
```

Report directory:

```text
app/build/reports/androidTests/connected/debug/
```

## 10. Persistence and storage review

PASS. The implementation remains local-first and uses DataStore Preferences. Current persisted launcher state includes home layout/page/grid/tile content, appearance settings, app-drawer preferences/favorites, and onboarding completion. Stored app/tile data has typed parsing and safe invalid-value handling. No network account, cloud, monetization, analytics, caregiver, parent, child, school, office, or kiosk state was introduced.

## 11. Permissions and product boundaries

PASS. The manifest contains network-state, Wi-Fi-state, and notification permission declarations used by current launcher UI. Contacts, phone, and SMS actions use launcher-level/external intents rather than implementing a full dialer or sending messages automatically. No system status-bar replacement, notification-shade replacement, Quick Settings replacement, device-owner, kiosk, or restricted mobile-data control was added.

## 12. CI and reproducibility

PASS locally. CI workflows call `./gradlew`, and the restored wrapper is tracked and executable. The local workflow files provide build/test/lint and emulator smoke jobs. A remote GitHub Actions run was not started during this local session.

## 13. Evidence paths

```text
APK: app/build/outputs/apk/debug/app-debug.apk
Runtime artifacts: docs/VERIFICATION/artifacts/2026-08-09/
Lint report: app/build/reports/lint-results-debug.html
Unit test report: app/build/reports/tests/testDebugUnitTest/index.html
Instrumentation report: app/build/reports/androidTests/connected/debug/index.html
Final foreground screenshot: docs/VERIFICATION/artifacts/2026-08-09/final-latest-launcher-home.png
Final activity evidence: docs/VERIFICATION/artifacts/2026-08-09/final-latest-home-activity.txt
Final HOME resolver evidence: docs/VERIFICATION/artifacts/2026-08-09/final-latest-home-resolve.txt
Final logcat: docs/VERIFICATION/artifacts/2026-08-09/final-latest-launcher-logcat.txt
```

## 14. Remaining non-blocking issues

- Legacy Wi-Fi APIs produce Kotlin deprecation warnings; they do not block build, lint, or runtime verification.
- The final runtime smoke used one available Android 15 emulator. A second physical/OEM target was not available after the earlier device disconnected.
- Remote GitHub Actions execution was not initiated from this session; local wrapper/CI configuration review passed.

## 15. Final quality-gate matrix

| Gate | Result |
|---|---|
| Gradle wrapper | PASS |
| Clean Gradle configuration | PASS |
| Clean build | PASS |
| Debug APK | PASS |
| Unit tests | PASS |
| Lint | PASS |
| Existing instrumentation tests | PASS |
| APK clean install | PASS |
| App launch | PASS |
| No launcher fatal crash | PASS |
| HOME candidate and HOME launch | PASS |
| Clean foreground launcher UI | PASS |
| Grid regression | PASS |
| Home persistence | PASS |
| App drawer | PASS |
| App launching | PASS |
| Theme/appearance persistence | PASS |
| Quick Access | PASS |
| Contacts/dialer shortcuts | PASS for safe UI/intent surface; no calls/messages issued |
| Widgets | PASS |
| Flashlight safety | PASS on Android 15 emulator |
| Reset | PASS |
| Documentation current | PASS |
| Final verification report | PASS |

## Final verdict

**GO.** All mandatory baseline engineering gates pass, the available instrumentation test passes, the launcher is verified as the active HOME target on Android 15, the clean foreground UI and implemented launcher-level flows were exercised, and the current documentation now reflects the implementation without weakening product guardrails.
