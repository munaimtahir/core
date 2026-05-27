# Roadmap

## Stage 0 — Documentation Foundation

Goal:

Create the project identity, scope, guardrails, AI-agent rules, testing strategy, and decision records.

Outputs:

- README
- project context
- greenfield policy
- baseline scope
- product guardrails
- architecture plan
- storage plan
- UI baseline
- testing strategy
- definition of done
- AI-agent instructions
- Copilot instructions
- Gemini instructions
- decision records

Exit criteria:

- documentation committed
- scope clearly locked
- AI-agent instructions available

## Stage 1 — Android Launcher Skeleton

Goal:

Create a clean Android project that can become a launcher.

Includes:

- Android project setup
- launcher intent configuration
- minimal home activity/screen
- basic build
- initial CI build

Exit criteria:

- app builds
- app launches
- launcher intent present
- build workflow passes

## Stage 2 — App Discovery and App List

Goal:

Detect installed apps and show a simple list.

Includes:

- installed app scanner
- app labels
- app icons
- alphabetical list
- tap to launch

Exit criteria:

- apps display
- icons display
- apps launch
- tests pass

## Stage 3 — Fixed Home Grid

Goal:

Create a stable home grid for selected apps/actions.

Includes:

- fixed grid
- selected home apps
- local persistence
- fallback handling
- app launch from home

Exit criteria:

- selected apps persist
- home grid stable
- no broken placeholders
- app launch works

## Stage 4 — Minimal Settings

Goal:

Add only the settings required for the baseline.

Includes:

- choose home apps
- choose basic theme
- reset home apps
- status/debug information

Exit criteria:

- settings persist
- reset works
- theme persists
- no state conflict

## Stage 5 — CI and Emulator Verification

Goal:

Verify baseline behavior through automation.

Includes:

- build workflow
- unit test workflow
- lint workflow
- emulator install/launch workflow
- screenshot/log artifacts
- final verification report

Exit criteria:

- all required workflows pass
- artifacts available
- baseline verdict documented

## Stage 6 — First Product Variant Planning

Start only after baseline GO.

Possible first variant:

- senior-friendly launcher

This stage should create a separate product scope before implementation.

## Stage 7 — Caregiver/Safety Layer Planning

Start only after the first product variant is stable.

Possible features:

- caregiver settings
- PIN-protected edit mode
- layout lock
- app visibility controls
- reset tools

## Stage 8 — Product Family Expansion

Start only after foundation and first variant are stable.

Possible directions:

- parent launcher
- child-safe launcher
- school launcher
- office launcher
- home launcher

## Roadmap rule

Do not skip stages.

Each stage should end with a GO, Conditional GO, or NO-GO verdict.
