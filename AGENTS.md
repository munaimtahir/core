# AGENTS.md

## Project

Repository:

```text
core
```

Project:

```text
Core Launcher Foundation
```

## Mission

Build a brand-new Android launcher foundation.

The first priority is a stable baseline, not feature expansion.

## Required reading before coding

Read:

1. `README.md`
2. `docs/PROJECT_CONTEXT.md`
3. `docs/GREENFIELD_POLICY.md`
4. `docs/BASELINE_SCOPE.md`
5. `docs/PRODUCT_GUARDRAILS.md`
6. `docs/ARCHITECTURE.md`
7. `docs/TESTING/TESTING_STRATEGY.md`
8. `docs/DEFINITION_OF_DONE.md`

## Mandatory first action

Create or update:

```text
copilot_session.md
```

Before making code changes.

## Greenfield rule

Do not reuse legacy code, architecture, workflows, UI flows, or state systems from any existing project.

## Baseline-only rule

During v0.1, do not implement:

- caregiver PIN
- hidden settings entry
- layout lock
- app hiding
- parent mode
- child mode
- school mode
- office mode
- remote sync
- cloud account
- premium unlock
- ads
- kiosk/device-owner behavior

## Verification rule

Run real commands.

Recommended minimum:

```bash
./gradlew clean assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

If a command fails, report it honestly.

## Handoff rule

Maintain `copilot_session.md` with:

- goal
- plan
- checklist
- files inspected
- files changed
- commands run
- verification results
- remaining issues
- next step

## Final report rule

End each session with:

- summary
- files changed
- commands run
- verification results
- remaining issues
- final verdict: GO / Conditional GO / NO-GO
