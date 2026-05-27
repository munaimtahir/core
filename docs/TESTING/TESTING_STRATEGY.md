# Testing Strategy

## Purpose

`core` must be tested from the beginning.

The baseline is not complete until it can be verified repeatedly.

## Required verification layers

### 1. Build

Command:

```bash
./gradlew clean assembleDebug
```

Required result:

```text
PASS
```

### 2. Unit tests

Command:

```bash
./gradlew testDebugUnitTest
```

Required result:

```text
PASS
```

### 3. Lint

Command:

```bash
./gradlew lintDebug
```

Required result:

```text
PASS
```

### 4. Emulator runtime

Required workflow:

- boot emulator
- install APK
- launch app
- verify app opens
- capture screenshot
- collect logcat
- upload artifacts

### 5. Manual smoke test

Required before baseline GO if a device is available:

- install app
- set as default launcher
- press Home
- open app list
- launch apps
- choose home apps
- restart app
- verify persistence
- change theme
- verify persistence
- reset settings

## Testing priorities

Test these early:

- app scanner does not crash
- icons have fallback
- labels have fallback
- launch failures are safe
- selected apps persist
- removed apps are handled
- theme does not revert
- reset produces safe defaults

## GitHub Actions workflows

Recommended workflows:

### Android Code CI

Runs:

- build
- unit tests
- lint

### Android Runtime Emulator CI

Runs:

- emulator boot
- APK install
- launch
- screenshot
- logcat
- artifact upload

## Artifact policy

Where practical, upload:

- build reports
- test reports
- lint reports
- screenshots
- logcat
- verification summary

## Failure policy

If a check fails:

- do not hide the failure
- do not delete the check
- do not mark the sprint complete
- diagnose the cause
- fix implementation or test
- rerun verification
- document the remaining blocker if unresolved

## Baseline GO criteria

Baseline testing reaches GO only when:

- build passes
- unit tests pass
- lint passes
- emulator runtime passes
- core manual smoke test passes or has a documented reason if not run
- final verification report exists
