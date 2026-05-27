# Live Debug Session

## Session purpose

Fast one-bug-at-a-time debugging while the user manually tests the app on a real device/emulator.

## Rules

- no PR per bug
- no commit per bug
- no push per bug
- no broad refactor per bug
- one bug at a time
- minimal fix
- focused check
- user verifies
- batch verification after bug group

## Bug ledger

| Bug ID | User symptom | Area | Files changed | Check run | Status |
|---|---|---|---|---|---|
| BUG-001 | All Apps list shows only some installed apps | App discovery / Package visibility | `app/src/main/AndroidManifest.xml`, `app/src/main/res/mipmap-anydpi-v26/ic_launcher*.xml` | `./gradlew assembleDebug` | pending user verification |
| BUG-002 | All Apps list scroll only works when dragging on the app name; dragging on the left/icon/empty space doesn't scroll | All Apps UI / touch handling | `app/src/main/java/com/easyui/core/MainActivity.kt` | `./gradlew assembleDebug` | pending user verification |
| BUG-003 | Home pages only change via Previous/Next buttons; swipe/slide does not change pages | Home UI / paging gesture | `app/src/main/java/com/easyui/core/MainActivity.kt` | `./gradlew assembleDebug` | pending user verification |
| BUG-004 | Back button on Home exits to a blank wallpaper screen instead of staying on Home | Navigation / back handling | `app/src/main/java/com/easyui/core/MainActivity.kt` | `./gradlew assembleDebug` | pending user verification |
