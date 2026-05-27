# core v0.1 Baseline Verification Report

Project: Core Launcher Foundation (`core`)

Target: core v0.1 — Basic Launcher Structure Lock

Date: 2026-05-27

## Scope Completed

- Home launcher intent + stable Home UI
- Installed app discovery (labels + icons) with safe fallbacks
- App launching with safe failure handling
- All Apps list (alphabetical)
- Fixed Home layout model (3 pages, 3x3 grid)
- Customize Home (assign / replace / remove / move / reset)
- Theme settings (Default / High Contrast / Large Text) + persistence
- Status/Debug screen
- Reset options (home-only vs all settings)
- Automated verification (build / unit tests / lint)
- GitHub Actions:
  - Android Code CI
  - Android Runtime Emulator CI

## Forbidden Scope Confirmed Not Implemented

- caregiver PIN
- hidden settings entry
- layout lock
- app hiding or restrictions
- parent/child/school/office modes
- cloud/login/ads/subscriptions/premium
- kiosk/device-owner behavior
- widgets/folders/categories/recommendations

## Architecture Summary

- `apps/`: PackageManager-backed discovery, launching, icon loading
- `home/`: deterministic fixed grid model + DataStore persistence
- `theme/`: theme ID + DataStore persistence (single source of truth)
- `storage/`: DataStore configuration
- UI: single-activity Compose with simple screen switching (no hidden flows)

## Storage Summary

DataStore Preferences keys:

- `theme_id`: selected theme
- `home_slot_{pageIndex}_{slotIndex}`: stored app component refs (`package|activity`)

Read behavior:

- invalid/missing values fall back safely (Default theme; empty slots)

## Verification Commands (Local)

Run:

```bash
./gradlew clean assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

Results:

- `clean assembleDebug`: PASS
- `testDebugUnitTest`: PASS
- `lintDebug`: PASS

## GitHub Actions

Workflows:

- `Android Code CI`: PASS (push run `26504621906`, PR run `26504623288`)
- `Android Runtime Emulator CI`: PASS (push run `26504622357`, PR run `26504623088`)

Artifacts (runtime workflow):

- screenshot
- logcat
- debug APK

## Known Limitations

- Emulator runtime workflow validates install + launch + no obvious crash; it does not (and cannot reliably) automate selecting default HOME on every device/OEM.

## Final Verdict

GO
