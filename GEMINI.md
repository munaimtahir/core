# GEMINI.md

## Project identity

You are working in a repository named:

```text
core
```

This is a brand-new Android launcher foundation project.

## Main goal

Build a stable baseline Android launcher before adding specialized launcher variants.

## Greenfield rule

Do not copy, migrate, or imitate legacy code, architecture, workflows, UI flows, or state systems from any existing project.

## Required first steps

Before coding:

1. Read `README.md`
2. Read `docs/PROJECT_CONTEXT.md`
3. Read `docs/GREENFIELD_POLICY.md`
4. Read `docs/BASELINE_SCOPE.md`
5. Read `docs/PRODUCT_GUARDRAILS.md`
6. Read `docs/ARCHITECTURE.md`
7. Read `docs/LAUNCHER_CUSTOMIZATION_SCOPE.md`
8. Create or update `copilot_session.md`

## Allowed baseline work

You may work on:

- Android launcher skeleton
- launcher intent
- home screen
- installed app scanner
- app icons
- app labels
- app launching
- simple app list
- fixed home grid
- local settings persistence
- basic theme persistence
- tests
- CI
- documentation

## Forbidden baseline work

Do not add:

- caregiver PIN
- hidden access
- layout lock
- app hiding
- parent mode
- child mode
- school mode
- office mode
- cloud sync
- remote dashboard
- premium logic
- ads
- subscriptions
- kiosk/device-owner claims
- real status bar replacement
- notification shade replacement
- Quick Settings replacement
- restricted Wi-Fi/mobile-data control
- full device lockdown claims

## Customization boundary

You may build launcher-level UI and behavior.

You may not build or claim Android system UI replacement.

A custom top bar inside the launcher is allowed.

Replacing the real Android status bar is not allowed.

A launcher quick access panel is allowed.

Replacing Android Quick Settings is not allowed.

Phone, contact, widget, search, and accessibility features are allowed only when they match the current stage and are documented.

## Verification

Use real commands.

Recommended:

```bash
./gradlew clean assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

Do not claim success unless verification passes.

## Required output

End every session with:

```text
Summary:
Files changed:
Commands run:
Verification:
Remaining issues:
Final verdict:
```

Final verdict must be:

- GO
- Conditional GO
- NO-GO
