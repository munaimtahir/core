# Copilot Session Handoff

## Project

```text
core
```

## Current goal

Live Debug Mode preparation: be ready to take one bug at a time (BUG-001, BUG-002, …) without creating a PR or committing/pushing per bug.

## Repo state (May 27, 2026)

- Branch: `main`
- Working tree: clean (as of this prep section; re-check before changes)

## Current status

Core v0.1 baseline implementation is present on `main`:

- Home launcher intent + stable Home screen
- All Apps list (PackageManager discovery, real labels/icons, alphabetical)
- App launching with safe failure handling
- Fixed Home grid model: 3 pages, 3x3
- Customize Home (assign/replace/remove/move/reset)
- Theme settings: Default / High Contrast / Large Text, persisted
- Status/Debug screen + Reset options
- CI: Android Code CI + Android Runtime Emulator CI
- Verification report: `docs/VERIFICATION/core-v0.1-baseline-report.md`

## Failure/fix log (Stage 8)

Emulator runtime CI failed with `/usr/bin/sh` incompatibilities:

- `set -o pipefail` is not supported by `sh` (dash): fixed by running the smoke script under `bash`.
- Multi-line quoting broke under `sh -c` (unterminated quote): fixed by rewriting the smoke script into a single-line `bash -lc ...` command.

## Commands run (recent)

- `./gradlew clean assembleDebug`: PASS
- `./gradlew testDebugUnitTest`: PASS
- `./gradlew lintDebug`: PASS

## GitHub Actions status (recent)

- Android Code CI (push): PASS (run `26504621906`)
- Android Code CI (PR): PASS (run `26504623288`)
- Android Runtime Emulator CI (push): PASS (run `26504622357`)
- Android Runtime Emulator CI (PR): PASS (run `26504623088`)

## Files inspected (prep)

- `.github/workflows/android-runtime-emulator-ci.yml`
- `.github/workflows/android-code-ci.yml`
- `docs/VERIFICATION/core-v0.1-baseline-report.md`
- `TASKS.md`
- `copilot_session.md`

## Remaining work (prep session)

- Live Debug Mode Preparation section (below)
- Create `docs/_debug/live_debug_session.md` ledger

## Live Debug Mode Preparation

### Current repo state

- Repo: `core`
- Branch: `main`
- Latest git status: clean (verify via `git status --porcelain=v1`)

### Android project structure summary

- Root Gradle: `build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`
- App module: `app/`
- Main activity: `app/src/main/java/com/easyui/core/MainActivity.kt`
- Manifest: `app/src/main/AndroidManifest.xml`
- Core packages:
  - `com.easyui.core.apps` (discovery, icon loading, launching)
  - `com.easyui.core.home` (fixed grid model + persistence)
  - `com.easyui.core.theme` (theme ID + persistence)
  - `com.easyui.core.storage` (DataStore wiring)

### App identity

- `namespace`: `com.easyui.core`
- `applicationId`: `com.easyui.core`
- Main Activity: `com.easyui.core/.MainActivity`

### Available Gradle commands

- Build debug: `./gradlew assembleDebug`
- Clean + build: `./gradlew clean assembleDebug`
- Unit tests: `./gradlew testDebugUnitTest`
- Lint: `./gradlew lintDebug`
- (If device/emulator available) instrumentation: `./gradlew connectedDebugAndroidTest`

### Available test commands

- JVM unit tests: `./gradlew testDebugUnitTest`
- Instrumentation smoke: `./gradlew connectedDebugAndroidTest`

### Available CI workflows

- `.github/workflows/android-code-ci.yml` (build + unit tests + lint)
- `.github/workflows/android-runtime-emulator-ci.yml` (emulator boot + install + launch + screenshot + logcat artifacts)

### Install/run method (local device/emulator)

- Detect devices: `adb devices -l`
- Build APK: `./gradlew assembleDebug`
- Install APK: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- Launch activity: `adb shell am start -n com.easyui.core/.MainActivity`
- Capture logcat: `adb logcat -d > runtime_logcat.txt`
- Capture screenshot: `adb exec-out screencap -p > runtime_screenshot.png`

### Debug artifact locations

- APK: `app/build/outputs/apk/debug/app-debug.apk`
- Lint HTML: `app/build/reports/lint-results-debug.html`
- Unit test report: `app/build/reports/tests/testDebugUnitTest/`
- Instrumentation results (if run): `app/build/reports/androidTests/connected/`
- Runtime workflow artifacts (CI): uploaded as `runtime-artifacts` (screenshot + logcat + APK)

### Live bug handling rules

- One bug at a time (assign BUG-001, BUG-002, …).
- Minimal fix, minimal files touched.
- No PR per bug.
- No commit/push per bug.
- Run the lightest relevant local check after each fix.
- Ask user to verify on device/emulator before moving to the next bug.

### Batch verification rules

- After ~5 bugs, or after finishing a feature area, run:
  - `./gradlew clean assembleDebug`
  - `./gradlew testDebugUnitTest`
  - `./gradlew lintDebug`
  - `./gradlew connectedDebugAndroidTest` (if available)

### Current checklist

- [ ] Create `docs/_debug/live_debug_session.md` ledger
- [ ] Keep this “Live Debug Mode Preparation” section current as bugs are handled

### Known risks / unknowns

- CI runtime smoke checks can surface unrelated emulator/system crashes; crash detection is best-effort and is scoped to `com.easyui.core`.
- Default HOME selection cannot be reliably automated across devices/OEMs; runtime CI validates install + launch only.
