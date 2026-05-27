# Baseline Scope

## Baseline version

```text
core v0.1
```

## Objective

Create a stable Android launcher baseline.

The baseline should be intentionally small.

## In scope

### 1. Launcher support

The app should be able to behave as an Android launcher.

Acceptance:

- launcher intent configured
- app appears as a home launcher option where supported
- app opens as the home surface when selected

### 2. Stable home screen

The app should display a reliable home screen.

Acceptance:

- no crash on launch
- no blank screen
- no broken placeholder UI
- no layout overflow on common screen sizes
- predictable behavior after restart

### 3. Installed app discovery

The app should detect launchable installed apps.

Acceptance:

- app labels load
- app package/activity references are available where needed
- unavailable apps are handled safely
- scanner does not crash on missing metadata

### 4. Real app icons

The app should display real app icons.

Acceptance:

- icons display in app list
- icons display on selected home tiles
- fallback icon appears when needed
- icons do not disappear after restart

### 5. App launching

The app should launch installed apps.

Acceptance:

- app list item opens the target app
- home tile opens the target app
- launch failure is handled safely
- app removal does not crash the launcher

### 6. Simple app list

The app should provide a simple app list.

Acceptance:

- alphabetical order
- icon
- label
- tap to launch
- no folders
- no categories
- no app restrictions in baseline

### 7. Fixed home grid

The app should provide a fixed home grid.

Acceptance:

- selected apps/actions appear in fixed positions
- selected apps persist
- layout remains stable
- no dynamic reshuffling
- no accidental placeholder artifacts

### 8. Local settings persistence

The app should persist minimum settings locally.

Acceptance:

- selected home apps persist after restart
- theme persists after restart
- reset restores safe defaults
- missing or invalid stored data is handled safely

### 9. Basic theme persistence

The app should support a small, reliable visual settings set.

Acceptance:

- palette selection applies immediately and persists:
  - System default
  - Light
  - Dark
- text size selection applies immediately and persists:
  - Small
  - Normal
  - Large
  - Larger
- text size scope is clearly labeled as Core Launcher UI text, not system-wide phone text
- each text size option includes a visible scale cue and preview
- icon size selection applies immediately and persists:
  - Normal
  - Large
- visual settings have one source of truth and do not revert randomly

### 9B. First-run onboarding setup

The app should provide a simple first-run onboarding screen.

Acceptance:

- shows current default Home app status conservatively
- provides a button to open Android's default Home app selection UI
- allows selecting the same theme/text/icon settings as the in-launcher Theme menu
- after onboarding, settings remain editable from the launcher menu

### 10. Automated verification

The project should include automated checks.

Acceptance:

- build passes
- unit tests pass
- lint passes
- emulator install/launch workflow passes
- artifacts/logs are available where practical

## Out of scope for v0.1

Do not implement:

- caregiver PIN
- hidden settings entry
- clock-tap access
- layout lock
- app hiding
- allowed-app restrictions
- parent mode
- child mode
- school mode
- office mode
- profile switching
- photo contacts
- emergency contact management
- backup/restore
- cloud sync
- remote dashboard
- accounts/login
- subscriptions
- ads
- premium unlock
- kiosk/device-owner features
- notification shade blocking
- OEM-specific control hacks

## Baseline completion statement

The baseline is complete only when the app can function as a simple launcher, display and launch installed apps, persist minimal settings, and pass verification.
