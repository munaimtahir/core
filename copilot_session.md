# Copilot Session Handoff

## Project

```text
core
```

## Current goal

Stage 1 CI closure + Stage 2: implement installed-app discovery and a simple app list (labels + icons + tap-to-launch) with safe fallbacks.

## Current status

Stage 1 implementation exists locally (Android skeleton + wrapper + minimal home + Android Code CI), but changes are not committed yet.

## Repo state (May 27, 2026)

- Local working tree contains new Android project files plus a CI workflow.
- `git status` shows untracked `app/`, `gradle/`, wrapper scripts, and workflow YAML (commit pending).

## Implementation plan (baseline-only)

1. Stage 1 CI closure:
   - Inspect `.github/workflows/android-code-ci.yml` and confirm it runs build, unit tests, and lint.
   - Run local equivalents to match CI:
     - `./gradlew clean assembleDebug`
     - `./gradlew testDebugUnitTest`
     - `./gradlew lintDebug`
   - Commit Stage 1 Android skeleton changes.
   - Commit the CI workflow change.
   - If the repo has a GitHub remote + `gh` access, verify the latest Actions run is green after push.
2. Stage 2 app discovery + app list:
   - Add a small app model (label, component, package name, activity name, icon load path).
   - Implement PackageManager-backed discovery:
     - query `ACTION_MAIN` + `CATEGORY_LAUNCHER`
     - load labels + icons with safe fallbacks
     - sort alphabetically
     - exclude this launcher app
   - Add app launching abstraction with safe failure handling.
   - Add a simple “All apps” screen reachable from Home:
     - list icon + label, alphabetical
     - tap launches app
     - launch failures don’t crash (show a simple error message)
   - Add unit tests for pure logic (sorting + fallback) and keep UI changes minimal.
3. Re-run verification commands:
   - `./gradlew clean assembleDebug`
   - `./gradlew testDebugUnitTest`
   - `./gradlew lintDebug`
4. Update this handoff with files changed, commands run, results, and GitHub Actions status (if verifiable).

## Active principle

Build the stable baseline first.

Do not add product-specific variants until the launcher foundation is verified.

## Greenfield reminder

Do not reuse legacy code, architecture, workflows, UI flows, or state systems from any existing project.

## Task checklist

- [x] Review baseline docs + agent rules
- [ ] Stage 1 CI closure: commit Android skeleton
- [ ] Stage 1 CI closure: commit Android Code CI workflow
- [ ] Stage 1 CI closure: verify GitHub Actions result (if remote/PR available)
- [ ] Stage 2: implement installed app discovery
- [ ] Stage 2: load app labels
- [ ] Stage 2: load app icons with fallback
- [ ] Stage 2: exclude self where appropriate
- [ ] Stage 2: app list UI (alphabetical) reachable from Home
- [ ] Stage 2: tap-to-launch with safe failure handling
- [ ] Add/adjust unit tests (practical, logic-focused)
- [ ] Run: `./gradlew clean assembleDebug`
- [ ] Run: `./gradlew testDebugUnitTest`
- [ ] Run: `./gradlew lintDebug`
- [ ] Update `copilot_session.md` with final results for this session

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

## Files changed

- `.github/workflows/android-code-ci.yml`
- `.gitignore`
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
- `app/src/main/java/com/easyui/core/ui/theme/Color.kt`
- `app/src/main/java/com/easyui/core/ui/theme/Theme.kt`
- `app/src/main/res/drawable/ic_launcher.xml`
- `app/src/main/res/drawable/ic_launcher_round.xml`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/themes.xml`
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

## Verification results

- `./gradlew clean assembleDebug`: PASS
- `./gradlew testDebugUnitTest`: PASS
- `./gradlew lintDebug`: PASS

## Remaining issues

Stage 1 changes are not committed yet.

Stage 2 is not implemented yet.

## Next recommended step

Commit Stage 1 work, verify CI configuration, then implement Stage 2 app discovery + app list + safe app launching.
