# Launcher Customization Scope

## Purpose

This document defines what customization is allowed in the `core` launcher foundation and future EasyUI Launcher Family products.

It separates:

1. Baseline launcher features to build first.
2. Allowed launcher-level customization to add after baseline stability.
3. Product-variant features to add only after the baseline receives a GO verdict.
4. Android system-level features that must not be promised or implemented as normal launcher features.

## Core principle

A launcher can redesign the Android home experience.

A normal launcher cannot fully redesign or replace Android system UI.

This means `core` can build a custom home screen, app list, app grid, themes, shortcuts, quick access panel, and top launcher information area.

It must not claim to replace the real Android status bar, notification shade, Quick Settings panel, system settings, mobile data controls, or full device lockdown behavior on normal consumer devices.

---

# 1. Build in `core v0.1`

These are mandatory baseline features.

## Launcher role

- App can behave as an Android home launcher.
- App appears as a launcher/home option where supported.
- App opens as the home surface when selected.
- App launches without a blank screen or crash.

## Stable home screen

- Stable home screen.
- Fixed home grid.
- App/action tiles.
- Real app icons.
- App labels.
- Safe fallback icon.
- Safe fallback label.
- No broken placeholders.
- No random lock icons.
- No layout overflow on common screen sizes.
- Predictable restart behavior.

## Installed app discovery

- Detect launchable installed apps.
- Load app label.
- Load app icon.
- Store package/activity reference where needed.
- Exclude self where appropriate.
- Handle missing app metadata safely.
- Handle empty app list safely.
- Handle removed/uninstalled apps safely.

## Simple app list

- Alphabetical app list.
- App icon.
- App label.
- Tap-to-launch behavior.
- Safe launch failure handling.

Do not include folders, app categories, restrictions, hidden apps, or smart filters in v0.1.

## App launching

- Launch app from app list.
- Launch app from home tile.
- Handle failed launch without crash.
- Handle removed app without crash.

## Fixed home grid

- Fixed grid definition.
- Selected apps appear in fixed positions.
- Selected apps persist after restart.
- Missing apps are removed or replaced safely.
- No dynamic reshuffling.
- No accidental visible placeholders.

## Minimal settings

- Choose home apps.
- Choose theme.
- Reset home apps/layout.
- Status/debug screen.

## Basic theme persistence

- Default theme.
- High contrast theme.
- Theme applies immediately.
- Theme persists after restart.
- Theme has one source of truth.
- No theme flicker.
- No random revert to old theme.

## Local storage

- Store selected home apps.
- Store selected theme.
- Store first-run flag only if needed.
- Store simple debug/version marker only if needed.
- Validate stored app references on read.
- Reset returns app to known safe defaults.

## Testing and verification

- Build passes.
- Unit tests pass.
- Lint passes.
- Emulator install/launch workflow passes.
- Screenshot artifact captured where practical.
- Logcat artifact captured where practical.
- Final baseline verification report written.

---

# 2. Allowed after baseline GO

These features are allowed launcher-level customizations, but they must not block or destabilize `core v0.1`.

## Home screen customization

Implemented in `Core Launcher MVP A-F + Runtime Patch`:

- Number of home pages (1-9).
- Grid size options (2x2, 3x3, 4x4).
- App assignment migration (Option A: Packing).
- Favorite contact tiles (Name + Phone).
- Local widgets (Clock, Date, Note).
- Label visibility toggle (Show/Hide).
- Page indicators.
- Top launcher information strip (Battery, Network).

Allowed after baseline:

- Tile size options.
- Icon size options.
- Label size options.
- Home screen search bar.
- Favorite apps row.
- Bottom dock row.
- Background color selection.
- Wallpaper/background image.
- Weather summary.
- Calendar/event tile.

Delay until later:

- Drag-and-drop placement.
- Advanced gestures.
- Dynamic layouts.
- Complex animations.

## App drawer customization

Implemented:

- Search installed apps by label.
- App drawer list/grid switch.
- Favorite apps section (long-click to toggle).
- Manual refresh action.

Allowed later:

- Recent apps section.
- Large-icon drawer mode.
- Compact drawer mode.
- Auto-refresh when apps are installed or removed.
- App folders.
- App categories.
- Manual grouping.
- Smart recommendations.

## Theme and appearance customization

Implemented:

- Light / Dark / High Contrast themes.
- Custom accent color presets (6 colors).
- Font size options.
- Icon size options.
- Tile shape options (Soft, Extra, Square).
- Reduced motion option.
- Label visibility toggle.

Product-specific themes must wait for their product variant:

- Senior-friendly theme.
- Child-friendly theme.
- School theme.
- Office/productivity theme.
- Home-device theme.

## Launcher quick access panel

Implemented:

- Quick access panel inside launcher.
- Wi-Fi settings shortcut.
- Bluetooth settings shortcut.
- Display settings shortcut.
- Sound/volume settings shortcut.
- Accessibility settings shortcut.
- Battery settings shortcut.
- Launcher Notification settings shortcut.
- Android Settings main screen shortcut.
- Camera shortcut.
- Phone shortcut.
- Messages shortcut.
- Browser shortcut.
- Flashlight toggle (with hardware check).

Important boundary:

These are launcher shortcuts or launcher actions. They do not replace the real Android Quick Settings panel.

## Custom top launcher information bar

Implemented:

- Custom top bar inside launcher.
- Time display.
- Date display.
- Battery percentage inside launcher.
- Wi-Fi/Network status summary.

Allowed later:

- Weather summary.
- Settings icon.
- Search icon.
- Notification shortcut icon.
- Profile/mode label.

Important boundary:

This is not the real Android status bar. It is a launcher-designed information area.

## Phone, dialer, and contact features

Implemented:

- Phone app tile.
- Dial number shortcut (ACTION_DIAL).
- Favorite contact tiles (Name + Phone).
- SMS shortcut.
- Contact shortcut picker (manual entry for MVP).

Allowed only as separate advanced work:

- Full custom dialer app.
- Default dialer replacement.
- In-call screen replacement.

These should not be part of the baseline launcher.

## Widgets

Implemented:

- Level 2: Built-in local widgets.
- Clock widget.
- Date widget.
- Notes placeholder widget.

Allowed later:

- Weather widget.
- Calendar widget.
- Custom launcher widgets.
- Third-party Android AppWidgets (AppWidgetHost).

Widgets must be delayed until the baseline is stable because widget hosting adds complexity.

## Accessibility customization

Allowed from baseline where practical:

- Large touch targets.
- Readable labels.
- Good contrast.
- Content descriptions.
- Simple wording.
- Scalable text.
- High contrast mode.

Allowed later:

- Extra-large mode.
- Reduced motion.
- Text-to-speech labels.
- Voice guidance.
- Senior readability preset.
- Child readability preset.

## Search and assistant-like features

Allowed later:

- Search installed apps.
- Search contacts.
- Search settings shortcuts.
- Search web.
- Voice search.
- AI assistant/search.

AI features must not enter the baseline.

---

# 3. Product-variant features

These are allowed only after the baseline receives a GO verdict.

## Senior launcher variant

Possible features:

- Extra-large home mode.
- Favorite contacts.
- Emergency contact tile.
- Medication/reminder shortcuts.
- Simplified phone/message access.
- Large clock/date area.
- High contrast senior theme.
- Reduced motion.
- Caregiver help shortcut.

## Guardian/caregiver variant

Possible features:

- Caregiver settings.
- PIN-protected edit mode.
- Layout lock.
- Prevent accidental home changes.
- App visibility controls.
- Allowed apps list.
- Emergency contacts.
- Status check screen.
- Caregiver support flow.

## Parent/child-safe variant

Possible features:

- Child-safe home screen.
- Allowed apps list.
- App visibility controls.
- Restricted settings access.
- Simple school/home mode.
- Parent-managed layout.
- Time-based rules only if technically reliable and clearly documented.

## School launcher variant

Possible features:

- School app list.
- Learning resources.
- Timetable shortcuts.
- LMS shortcut.
- Exam/practice shortcut.
- Teacher/admin configured layout.

## Office launcher variant

Possible features:

- Productivity app layout.
- Work shortcuts.
- Communication shortcuts.
- Calendar/meeting tile.
- Document/workflow shortcuts.

---

# 4. Do not build as normal launcher features

The following must not be implemented or promised in the normal launcher baseline.

## Android system UI replacement

Do not build or claim:

- Real Android status bar replacement.
- Real notification shade replacement.
- Real Quick Settings replacement.
- OEM control center replacement.
- Full system settings replacement.
- Lock screen replacement as part of baseline.

## Restricted system control

Do not build or claim:

- Direct Wi-Fi toggle control on modern Android.
- Direct mobile data toggle control.
- Airplane mode toggle control.
- Forced Bluetooth control where restricted.
- Forced notification shade blocking.
- Forced system navigation blocking.
- Complete prevention of Android settings access.

## Full lockdown behavior

Do not build or claim in normal launcher mode:

- Kiosk enforcement.
- Enterprise MDM behavior.
- Device-owner behavior.
- Complete parental control.
- Complete caregiver control.
- Complete device restriction.
- Full anti-exit behavior.

These may be explored later only as a separate managed-device or kiosk-mode track.

## Monetization and cloud

Do not build in baseline:

- Account login.
- Cloud sync.
- Remote dashboard.
- Ads.
- Subscription.
- Premium unlock.
- Payment logic.
- Licensing checks.

---

# 5. Feature staging rule

Every allowed feature must be assigned to one of four stages:

1. `core v0.1 baseline`
2. `post-baseline launcher customization`
3. `product variant`
4. `separate advanced/managed-device project`

No feature may be added just because Android allows it.

A feature can enter implementation only if:

- it fits the current stage;
- it does not destabilize the launcher baseline;
- it can be tested;
- it has safe fallback behavior;
- it does not violate Android system boundaries;
- it does not create false product claims.

---

# 6. Final product boundary statement

`core` will include every reasonable launcher-level customization Android allows, but implementation will be staged.

The first version will build only the stable launcher foundation: launcher role, stable home screen, installed app discovery, real app icons and labels, app launching, simple app list, fixed home grid, local persistence, basic theme persistence, reset, status/debug screen, and automated verification.

After baseline GO, the project may add grid options, page options, icon/text sizing, quick access panel, top launcher information bar, widgets, search, folders, contacts, and accessibility presets.

Product-specific features such as senior mode, guardian mode, parent mode, child-safe mode, school mode, office mode, caregiver settings, PIN protection, layout lock, emergency contacts, and app visibility controls must wait until the baseline is stable.

The launcher will not claim to replace Android system UI. It will not promise control over the real status bar, notification shade, Quick Settings panel, Wi-Fi/mobile-data toggles, or full device lockdown on normal consumer devices.
