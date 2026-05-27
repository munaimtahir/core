# TASKS

## Stage 0 — Documentation Foundation

- [x] Create repository named `core`
- [x] Add documentation pack
- [x] Review `README.md`
- [x] Review `docs/PROJECT_CONTEXT.md`
- [x] Review `docs/GREENFIELD_POLICY.md`
- [x] Review `docs/BASELINE_SCOPE.md`
- [x] Review `docs/PRODUCT_GUARDRAILS.md`
- [x] Review `AGENTS.md`
- [x] Review `GEMINI.md`
- [x] Review `.github/copilot-instructions.md`
- [x] Commit documentation as first commit

## Stage 1 — Android Project Skeleton

- [x] Create clean Android project
- [x] Confirm greenfield structure
- [x] Configure launcher intent
- [x] Add minimal home screen
- [x] Run build
- [x] Add initial CI workflow
- [x] Update `copilot_session.md`

## Stage 2 — App Discovery

- [x] Implement installed app discovery
- [x] Load app labels
- [x] Load app icons
- [x] Add fallback icon behavior
- [x] Exclude self where appropriate
- [x] Add tests
- [x] Verify build/test/lint

## Stage 3 — App List

- [x] Build simple alphabetical app list
- [x] Show icons and labels
- [x] Launch apps on tap
- [x] Handle launch failures safely
- [x] Add tests where practical

## Stage 4 — Fixed Home Grid

- [x] Define fixed home grid (3 pages, 3x3)
- [x] Allow selecting home apps
- [x] Persist selected home apps
- [x] Show icons and labels on home
- [x] Launch apps from home
- [x] Reset to safe defaults
- [x] Verify restart persistence

## Stage 5 — Theme Persistence

- [x] Add minimal theme list (Default / High Contrast / Large Text)
- [x] Persist selected theme
- [x] Verify no flicker/revert behavior (manual + CI)
- [x] Add tests for theme state

## Stage 6 — CI and Emulator Runtime

- [x] Add Android Code CI
- [x] Add Android Runtime Emulator CI
- [x] Capture screenshot artifact
- [x] Capture logcat artifact
- [x] Iterate until green
- [x] Produce baseline verification report stub

## Stage 7 — Baseline Verdict

- [x] Run complete local test suite (build/unit/lint)
- [x] Run emulator workflow (GitHub Actions)
- [ ] Run manual smoke test if available
- [x] Finalize report with PASS/FAIL run IDs + verdict
- [x] Mark baseline GO / Conditional GO / NO-GO
