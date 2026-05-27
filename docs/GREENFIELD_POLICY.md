# Greenfield Policy

## Status

Locked.

## Decision

`core` is a brand-new greenfield project.

It must not reuse code, architecture, workflows, state systems, UI flows, or implementation structures from any existing launcher or app project.

## Meaning of greenfield

For this project, greenfield means:

- new repository
- new architecture
- new implementation
- new documentation
- new testing foundation
- new CI setup
- new baseline decisions

## Allowed references

Developers and AI agents may use general Android documentation, official platform guidance, and standard Android development practices.

They may also use current project documents inside this repository.

## Not allowed

Do not:

- copy source files from another project
- copy folder structure from another project
- copy previous state-management design
- copy previous onboarding design
- copy previous launcher UI flow
- copy previous settings flow
- copy previous caregiver or mode logic
- reuse old tests without redesigning them for `core`
- describe the project as a continuation of another app
- describe the project as a repair of another app
- describe the project as a migrated version of another app

## Agent rule

Any AI coding agent must treat the repository as new.

If a suggested implementation depends on legacy code or assumptions from another project, reject that implementation and create a clean version based on the current documentation.

## Pull request rule

Every pull request must confirm:

- no legacy code copied
- no legacy architecture copied
- no old workflow imported
- no out-of-scope product feature added

## Final principle

`core` must stand on its own.
