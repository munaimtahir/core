# UI Baseline

## Purpose

Define the minimum UI direction for `core v0.1`.

## UI goal

The UI should be stable, readable, and easy to test.

It should avoid decorative complexity.

## Home screen

The home screen should include:

- clear time/date area if included
- fixed grid area
- app/action tiles
- app icons
- app labels
- safe fallback visuals

## Home grid rules

The home grid must:

- stay fixed
- stay within 3 pages maximum in v0.1
- avoid dynamic reshuffling
- avoid empty visible placeholders
- avoid complex gestures
- avoid hidden edit behavior
- remain stable across common screen sizes

## App list

The app list should show:

- app icon
- app label
- alphabetical order
- tap-to-launch behavior

Do not add baseline app-list complexity such as:

- folders
- categories
- hidden apps
- restricted apps
- mode filters
- smart recommendations

## Settings

Baseline settings should include only:

- choose home apps
- choose theme
- reset home apps
- status/debug screen

## Theme system

Baseline should start with a very small theme set.

Recommended:

- Default
- High contrast

Add more themes only after theme persistence is stable.

## Accessibility

The UI should support:

- readable labels
- large touch targets
- good contrast
- scalable text where practical
- simple wording
- content descriptions where appropriate

## Motion

Use minimal motion.

Avoid:

- bouncy animations
- playful effects
- slow transitions
- animation masking of state problems

## UI anti-patterns

Do not show:

- random lock icons
- unfinished placeholders
- inconsistent cards
- broken app icons
- conflicting color systems
- hidden controls needed for basic use
