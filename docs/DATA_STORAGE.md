# Data Storage

## Goal

Store only the minimum data needed for the baseline launcher to work reliably.

## Storage approach

Use local device storage.

Recommended:

```text
Jetpack DataStore Preferences
```

## Allowed baseline data

| Data | Purpose |
|---|---|
| selected home app references | remember home grid |
| selected theme palette/text/icon settings | remember visual preference |
| first-run flag if needed | avoid repeating basic intro |
| simple debug/version marker if needed | troubleshooting |

## Not allowed in baseline storage

Do not store:

- caregiver PIN
- child profile
- parent restrictions
- school mode settings
- office mode settings
- remote caregiver links
- cloud identity
- premium state
- subscription state
- ad identifiers
- analytics profile

## Data rules

### 1. Store minimal state

Do not store derived data if it can be safely recalculated.

### 2. Validate on read

Stored app references must be checked against currently installed apps.

### 3. Safe fallback

If data is missing, corrupted, or invalid, the launcher must return to safe defaults.

### 4. Reset behavior

Reset must return the app to a known baseline state.

### 5. Theme stability

Theme selection must be stored in one place and read consistently.

No competing theme state should exist.

## Current keys (v0.1)

DataStore Preferences keys:

- `theme_palette`
- `text_size`
- `icon_size`
- `home_slot_{pageIndex}_{slotIndex}` (value: `packageName|activityName`)

## Privacy

Baseline data stays on device.

No baseline data should leave the device.
