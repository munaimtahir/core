# GitHub Copilot Instructions — core

## Repository identity

This repository is named `core`.

It is a brand-new Android launcher foundation project.

## Greenfield policy

Do not reuse legacy code, architecture, workflows, UI flows, or state systems from any existing project.

All suggestions should be based on this repository's current documentation and current code.

## Current development goal

Build a stable launcher baseline.

The baseline includes:

- launcher/default home support
- stable home screen
- installed app scanning
- real app labels
- real app icons
- app launching
- simple app list
- fixed home grid
- local settings persistence
- basic theme persistence
- automated verification

## Do not suggest baseline features that are out of scope

Do not suggest or implement:

- caregiver PIN
- hidden access
- layout lock
- app hiding
- parent mode
- child mode
- school mode
- office mode
- remote sync
- cloud account
- subscription
- ads
- premium unlock
- kiosk/device-owner behavior

## Code style guidance

Prefer:

- simple architecture
- clear state ownership
- small functions
- safe fallbacks
- testable logic
- readable naming
- minimal UI complexity

Avoid:

- duplicated state
- hidden behavior
- broad unverified refactors
- manufacturer-specific assumptions
- feature creep

## Testing expectation

When behavior changes, tests should be added or updated.

Do not remove tests only to make CI pass.

## Documentation expectation

When scope, behavior, or architecture changes, update relevant documentation.

Important files:

- `docs/BASELINE_SCOPE.md`
- `docs/ARCHITECTURE.md`
- `docs/DATA_STORAGE.md`
- `docs/UI_BASELINE.md`
- `docs/TESTING/TESTING_STRATEGY.md`
- `copilot_session.md`
