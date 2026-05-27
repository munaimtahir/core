# Project Context — core

## Summary

`core` is a brand-new Android launcher foundation project.

It will provide the shared foundation for a future family of simplified launcher products.

## Purpose

The purpose of `core` is to create a stable Android launcher baseline before building specialized launcher experiences.

The foundation must be simple, reliable, local-first, and easy to test.

## Core concept

A launcher is the user's home surface on Android.

For this project, the baseline launcher should do the basics extremely well:

- open reliably
- show a stable home screen
- detect installed apps
- show real app icons and labels
- launch apps
- remember selected home apps
- remember basic visual preferences
- recover safely from missing apps or storage issues

## Future product direction

The foundation may later support multiple products, such as:

- senior-friendly launcher
- caregiver-assisted launcher
- parent-supervised launcher
- child-safe launcher
- school-focused launcher
- office/productivity launcher
- simplified home-device launcher

These are future layers.

They are not part of the first baseline.

## Project philosophy

- Stability before customization.
- Simplicity before power.
- Local-first before cloud.
- Visible workflows before hidden workflows.
- Testable behavior before advanced behavior.
- Small scope before product expansion.
- Clear boundaries before marketing claims.

## Core product truth

`core` simplifies the Android home experience.

It does not claim to fully control Android system behavior across all consumer devices.

## Greenfield status

`core` is a complete greenfield project.

It should not reuse existing codebases, legacy architecture, old workflows, old state systems, or copied UI flows.

All architecture and implementation should be designed specifically for this project and its baseline scope.

## First success condition

The first success condition is a stable launcher baseline that passes:

- build verification
- unit tests
- lint
- emulator install/launch testing
- basic manual smoke testing when available
- documentation review

## First non-goal

The first non-goal is feature richness.

This project must not add advanced product features before the launcher foundation is stable.
