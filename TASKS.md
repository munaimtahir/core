# TASKS

## Stage 0 — Documentation Foundation

- [ ] Create repository named `core`
- [ ] Add documentation pack
- [ ] Review `README.md`
- [ ] Review `docs/PROJECT_CONTEXT.md`
- [ ] Review `docs/GREENFIELD_POLICY.md`
- [ ] Review `docs/BASELINE_SCOPE.md`
- [ ] Review `docs/PRODUCT_GUARDRAILS.md`
- [ ] Review `AGENTS.md`
- [ ] Review `GEMINI.md`
- [ ] Review `.github/copilot-instructions.md`
- [ ] Commit documentation as first commit

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

- [ ] Define fixed home grid
- [ ] Allow selecting home apps
- [ ] Persist selected home apps
- [ ] Show icons and labels on home
- [ ] Launch apps from home
- [ ] Reset to safe defaults
- [ ] Verify restart persistence

## Stage 5 — Theme Persistence

- [ ] Add minimal theme list
- [ ] Persist selected theme
- [ ] Verify no flicker/revert behavior
- [ ] Add tests for theme state

## Stage 6 — CI and Emulator Runtime

- [ ] Add Android Code CI
- [ ] Add Android Runtime Emulator CI
- [ ] Capture screenshot artifact
- [ ] Capture logcat artifact
- [ ] Iterate until green
- [ ] Produce final baseline verification report

## Stage 7 — Baseline Verdict

- [ ] Run complete test suite
- [ ] Run emulator workflow
- [ ] Run manual smoke test if available
- [ ] Write final report
- [ ] Mark baseline GO / Conditional GO / NO-GO
