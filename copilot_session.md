# Copilot Session Handoff

## Project

```text
core
```

## Current goal

Complete baseline MVP for `core v0.1` (Stages 1B through 9): 3-page fixed Home grid + customization + DataStore persistence + theme persistence + status/debug + reset + emulator runtime CI + final verification report.

## Current status

Stage 1B + Stage 2 + Stage 3 are implemented on branch `stage2-app-discovery` (PR #1 open).

Remaining baseline stages to implement:
- Stage 4: fixed 3-page Home grid
- Stage 5: manual app organization/customization
- Stage 6: theme persistence (Default / High Contrast / Large Text)
- Stage 7: status/debug + reset options
- Stage 8: emulator runtime CI workflow (install + launch + screenshot + logcat)
- Stage 9: final baseline verification report

## Repo state (May 27, 2026)

- Branch: `stage2-app-discovery`
- Stage 1 and Stage 2 changes committed and pushed.
- Working tree clean.

## Implementation plan (baseline-only)

1. Stage 1B preflight:
   - Confirm Gradle + manifest + existing Code CI still pass locally.
2. Stage 4 + 5 (Home grid + Customize):
   - Define fixed grid model (3 pages, 3x3 per page).
   - Implement persistent Home layout model (page+slot -> optional app ref).
   - Build Home UI with page navigation, grid tiles, All Apps entry, Customize entry.
   - Build Customize UI:
     - select page
     - select slot
     - assign/replace/remove
     - move between slots/pages (two-step select source, then destination)
     - reset home layout
3. Stage 6 (Theme persistence):
   - Implement theme ID model + DataStore-backed repository as single source of truth.
   - Add Theme Settings screen with exactly: Default / High Contrast / Large Text.
4. Stage 7 (Status/Debug + Reset):
   - Add Status screen (version, app count, theme, home slot count, conservative launcher status).
   - Add Reset screen (reset home only, reset all baseline settings) with confirmation.
5. Stage 8 (Emulator runtime CI):
   - Add workflow to boot emulator, install APK, launch activity, capture screenshot + logcat, upload artifacts.
6. Stage 9 (Final report):
   - Add `docs/VERIFICATION/core-v0.1-baseline-report.md` describing verification and known limitations.
7. Verification:
   - `./gradlew clean assembleDebug`
   - `./gradlew testDebugUnitTest`
   - `./gradlew lintDebug`
   - (If instrumentation tests added) `./gradlew connectedDebugAndroidTest` or document why not run.
8. Update this handoff with files changed, commands run, results, and GitHub Actions status.

## Active principle

Build the stable baseline first.

Do not add product-specific variants until the launcher foundation is verified.

## Greenfield reminder

Do not reuse legacy code, architecture, workflows, UI flows, or state systems from any existing project.

## Task checklist

- [x] Review baseline docs + agent rules
- [x] Stage 1B: commit Android skeleton
- [x] Stage 1B: commit Android Code CI workflow
- [x] Stage 1B: verify GitHub Actions result (remote run)
- [x] Stage 2: installed app discovery
- [x] Stage 3: All Apps list UI + app launching
- [ ] Stage 4: fixed Home grid (3 pages) + stable navigation
- [ ] Stage 5: Customize Home (assign/replace/remove/move/reset)
- [ ] Stage 6: Theme settings + persistence (Default/High Contrast/Large Text)
- [ ] Stage 7: Status/Debug screen + Reset options
- [ ] Stage 8: Emulator runtime CI workflow
- [ ] Stage 9: Final baseline verification report
- [x] Add/adjust unit tests (practical, logic-focused)
- [ ] Run: `./gradlew clean assembleDebug`
- [ ] Run: `./gradlew testDebugUnitTest`
- [ ] Run: `./gradlew lintDebug`
- [ ] Update `copilot_session.md` with final results for this session

## Commands planned (this session)

- `./gradlew clean assembleDebug`
- `./gradlew testDebugUnitTest`
- `./gradlew lintDebug`
- `gh run watch ...` (Android Code CI + emulator runtime CI once added)

## Known risks

- DataStore schema/serialization errors causing crashes or state conflicts (mitigation: pure helpers + safe fallback on read).
- Home grid UI overflow on small screens (mitigation: fixed grid with adaptive sizing + ellipsized labels).
- Emulator runtime CI flakiness (mitigation: conservative timeouts + artifacts for diagnosis).

## Files inspected

- `README.md`
- `AGENTS.md`
- `GEMINI.md`
- `.github/copilot-instructions.md`
- `docs/PROJECT_CONTEXT.md`
- `docs/GREENFIELD_POLICY.md`
- `docs/BASELINE_SCOPE.md`
- `docs/PRODUCT_GUARDRAILS.md`
- `docs/ARCHITECTURE.md`
- `docs/DATA_STORAGE.md`
- `docs/UI_BASELINE.md`
- `docs/TESTING/TESTING_STRATEGY.md`
- `docs/DEFINITION_OF_DONE.md`
- `TASKS.md`
- `.github/workflows/README.md`
- `.github/workflows/android-code-ci.yml`

## Files changed

- `.github/workflows/android-code-ci.yml`
- `.gitignore`
- `TASKS.md`
- `build.gradle.kts`
- `settings.gradle.kts`
- `gradle.properties`
- `gradle/libs.versions.toml`
- `gradle/wrapper/gradle-wrapper.properties`
- `gradle/wrapper/gradle-wrapper.jar`
- `gradlew`
- `gradlew.bat`
- `app/build.gradle.kts`
- `app/proguard-rules.pro`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/easyui/core/MainActivity.kt`
- `app/src/main/java/com/easyui/core/apps/AppDiscovery.kt`
- `app/src/main/java/com/easyui/core/apps/AppIconLoader.kt`
- `app/src/main/java/com/easyui/core/apps/AppLauncher.kt`
- `app/src/main/java/com/easyui/core/apps/AppSorting.kt`
- `app/src/main/java/com/easyui/core/apps/LaunchableApp.kt`
- `app/src/main/java/com/easyui/core/apps/PackageManagerAppDiscovery.kt`
- `app/src/main/java/com/easyui/core/apps/PackageManagerAppIconLoader.kt`
- `app/src/main/java/com/easyui/core/apps/PackageManagerAppLauncher.kt`
- `app/src/main/java/com/easyui/core/ui/theme/Color.kt`
- `app/src/main/java/com/easyui/core/ui/theme/Theme.kt`
- `app/src/main/res/drawable/ic_app_placeholder.xml`
- `app/src/main/res/drawable/ic_launcher.xml`
- `app/src/main/res/drawable/ic_launcher_round.xml`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/themes.xml`
- `app/src/test/java/com/easyui/core/apps/AppSortingTest.kt`
- `app/src/test/java/com/easyui/core/ExampleUnitTest.kt`
- `copilot_session.md`

## Commands run

- `ls -la`
- `sed -n ...` (read docs listed above)
- `find . -maxdepth 3 -type f` (repo inventory)
- `java -version`
- `gradle -v`
- `/tmp/gradle-dist/gradle-8.7/bin/gradle wrapper --gradle-version 8.7`
- `./gradlew clean assembleDebug` (PASS)
- `./gradlew testDebugUnitTest` (PASS)
- `./gradlew lintDebug` (PASS)
- `git switch -c stage2-app-discovery`
- `git commit ...` (Stage 1 split commits + CI trigger tweak)
- `git push -u origin stage2-app-discovery`
- `gh pr create` (PR #1)
- `gh run watch 26489357539` (Android Code CI push run PASS)
- `gh run watch 26489651456` (Android Code CI push run PASS)

## Verification results

- `./gradlew clean assembleDebug`: PASS
- `./gradlew testDebugUnitTest`: PASS
- `./gradlew lintDebug`: PASS

## GitHub Actions result

- Android Code CI (push on `stage2-app-discovery`): PASS (run `26489768186`)
- Android Code CI (PR #1): PASS (run `26489652450`)

## Remaining issues

- None known for Stage 2 baseline discovery/list behavior.

## Next recommended step

Proceed to Stage 4: fixed home grid selection + persistence (after confirming Stage 2 behavior on a device/emulator).
