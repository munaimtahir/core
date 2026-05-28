# core

`core` is a brand-new Android launcher foundation project.

It is a clean greenfield project created to build a stable, minimal, tested launcher base before any product-specific launcher variants are developed.

## Internal identity

Repository name:

```text
core
```

Internal project name:

```text
Core Launcher Foundation
```

Future product family:

```text
EasyUI Launcher Family
```

Possible future products may include:

- EasyUI Senior Launcher
- EasyUI Guardian Launcher
- EasyUI Parent Launcher
- EasyUI School UI
- EasyUI Office UI
- EasyUI Home UI

## What this project is

`core` is the technical foundation for a future family of simplified Android launcher products.

The first goal is not to create a feature-rich launcher.

The first goal is to create a reliable launcher baseline that can:

- act as an Android home launcher
- display a stable home screen
- discover installed apps
- show real app labels and icons
- launch apps reliably
- provide a simple app list
- provide a fixed home grid
- persist basic local settings
- support basic theme persistence
- pass automated build, lint, unit, and emulator checks

## What this project is not

`core` is not yet:

- a senior launcher
- a caregiver launcher
- a parent-control launcher
- a child-safe launcher
- a school launcher
- an office launcher
- a kiosk product
- an enterprise device-management system
- a cloud-managed product
- a monetized product build

## Greenfield policy

This is a brand-new project.

Do not reuse legacy code, architecture, workflows, state systems, UI flows, or implementation structures from any existing launcher or app project.

This project should make its own technical decisions based on its current baseline scope.

## Development principle

Build the smallest reliable launcher first.

Product variants and advanced features come later only after the baseline is stable and verified.

## Documentation map

Important files:

- `docs/PROJECT_CONTEXT.md`
- `docs/GREENFIELD_POLICY.md`
- `docs/BASELINE_SCOPE.md`
- `docs/PRODUCT_GUARDRAILS.md`
- `docs/LAUNCHER_CUSTOMIZATION_SCOPE.md`
- `docs/ROADMAP.md`
- `docs/ARCHITECTURE.md`
- `docs/DATA_STORAGE.md`
- `docs/UI_BASELINE.md`
- `docs/TESTING/TESTING_STRATEGY.md`
- `docs/DEFINITION_OF_DONE.md`
- `AGENTS.md`
- `GEMINI.md`
- `.github/copilot-instructions.md`
- `copilot_session.md`
- `TASKS.md`
