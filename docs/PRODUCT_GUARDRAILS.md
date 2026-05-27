# Product Guardrails

## Purpose

This file protects the project from scope creep and unrealistic product claims.

## Product truth

`core` simplifies the Android home experience.

It does not fully control Android system behavior across all consumer devices.

## Guardrail 1 — No full-lockdown promise

Do not claim that the app can fully block Android settings, system UI, notification shade, OEM menus, or all non-launcher behavior on normal consumer devices.

## Guardrail 2 — No enterprise MDM positioning

Do not describe the baseline as:

- mobile device management
- enterprise lockdown
- kiosk enforcement
- complete parental control
- complete device control

## Guardrail 3 — No cloud dependency in baseline

The baseline must work without:

- account
- login
- backend
- cloud sync
- web dashboard
- remote caregiver panel

## Guardrail 4 — No monetization in baseline

The baseline must not include:

- ads
- subscriptions
- premium unlock
- payment logic
- licensing checks

## Guardrail 5 — No product variants before baseline

Do not add senior, caregiver, parent, child, school, office, or home variant logic in v0.1.

## Guardrail 6 — No hidden workflows before visible workflows

Hidden entry methods, protected settings, or restricted modes should not exist before the basic launcher is stable.

## Guardrail 7 — No OEM-specific core hacks

OEM-specific behavior may vary.

Do not build the foundation around manufacturer-specific assumptions.

## Guardrail 8 — No complex customization before stability

Avoid:

- many themes
- complex grids
- widgets
- gestures
- advanced sorting
- multiple modes
- dynamic layouts

## Feature admission rule

A feature can enter the project only if:

- it fits the current stage
- it does not make the baseline harder to stabilize
- it can be tested
- it is documented
- it does not violate the greenfield policy

## Final principle

If a feature makes the baseline less predictable, delay it.
