# Architecture

## Status

Initial architecture guidance for a brand-new project.

## Architecture goals

The architecture must be:

- simple
- testable
- local-first
- modular enough for future variants
- understandable by humans and AI agents
- resistant to state conflicts
- easy to verify through CI

## Baseline modules

### 1. Launcher shell

Responsible for:

- Android entry point
- launcher/home intent
- safe startup
- baseline navigation container

### 2. App discovery

Responsible for:

- finding launchable apps
- loading labels
- loading icons
- safe fallback handling

Implementation (current):

- `app/src/main/java/com/easyui/core/apps/`

### 3. App launcher

Responsible for:

- resolving launch intents
- opening target apps
- handling launch failure safely

Implementation (current):

- `app/src/main/java/com/easyui/core/apps/`

### 4. Home model

Responsible for:

- selected home slots
- fixed grid definition
- default layout
- validation of selected apps

Implementation (current):

- `app/src/main/java/com/easyui/core/home/`

### 5. Settings storage

Responsible for:

- selected home apps
- selected theme
- safe reset
- storage fallback

Implementation (current):

- DataStore: `app/src/main/java/com/easyui/core/storage/`

### 6. Theme system

Responsible for:

- small theme list
- stable theme state
- one source of truth
- consistent visual tokens

Implementation (current):

- `app/src/main/java/com/easyui/core/theme/`

### 7. Baseline UI

Responsible for:

- home screen
- app list
- minimal settings
- status/debug screen

### 8. Testing support

Responsible for:

- test tags
- deterministic checks
- emulator workflow support
- screenshot/log capture

## State rules

### One source of truth

Each persistent state must have only one source of truth.

Examples:

- selected home apps: one source
- selected theme: one source
- app list: derived from app discovery
- launch result: returned by app launcher

### UI is not storage

UI should display and update state through defined state paths.

Do not store critical state only inside UI components.

### Validate stored references

Stored app references may become invalid if apps are uninstalled.

The launcher must handle this safely.

## Error handling

The launcher home screen should not crash because of:

- missing icon
- missing label
- removed app
- failed launch intent
- storage read failure
- empty app list

Use safe fallbacks.

## Recommended technical direction

Preferred baseline stack:

- Kotlin
- Jetpack Compose
- AndroidX
- DataStore Preferences for small local settings
- Gradle Kotlin DSL if practical
- JUnit for unit tests
- Android instrumentation/UI tests when needed
- GitHub Actions for CI

## Avoid in baseline

Avoid:

- complex dependency injection unless needed
- large architecture frameworks too early
- multiple product flavors too early
- hidden mode logic
- remote services
- unnecessary databases
- advanced permissions
- manufacturer-specific workarounds as core logic

## Architecture review checklist

Before declaring architecture ready, confirm:

- app discovery location is clear
- icon loading location is clear
- launch logic location is clear
- settings storage location is clear
- theme source of truth is clear
- home selection model is clear
- tests can cover core logic
