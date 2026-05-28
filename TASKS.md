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

- [ ] Create clean Android project
- [ ] Confirm greenfield structure
- [ ] Configure launcher intent
- [ ] Add minimal home screen
- [ ] Run build
- [ ] Add initial CI workflow
- [ ] Update `copilot_session.md`

## Stage 2 — App Discovery

- [ ] Implement installed app discovery
- [ ] Load app labels
- [ ] Load app icons
- [ ] Add fallback icon behavior
- [ ] Exclude self where appropriate
- [ ] Add tests
- [ ] Verify build/test/lint

## Stage 3 — App List

- [ ] Build simple alphabetical app list
- [ ] Show icons and labels
- [ ] Launch apps on tap
- [ ] Handle launch failures safely
- [ ] Add tests where practical

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

## Stage 8 — Post-Baseline Launcher Customization Planning

- [ ] Review `docs/LAUNCHER_CUSTOMIZATION_SCOPE.md`
- [ ] Confirm baseline GO verdict exists
- [ ] Select first post-baseline customization group
- [ ] Decide whether to start with grid/page options, top bar, quick access panel, or search
- [ ] Confirm selected features are launcher-level only
- [ ] Confirm no system UI replacement behavior is included
- [ ] Confirm no caregiver/parent/child/school/office variant logic is mixed into generic core
- [ ] Create implementation sprint prompt
- [ ] Update `copilot_session.md`

## Stage 9 — Product Variant Planning

- [ ] Select first product variant
- [ ] Create separate product scope
- [ ] Define variant-specific features
- [ ] Confirm baseline remains reusable
- [ ] Confirm variant does not pollute core foundation
- [ ] Define tests for variant behavior
- [ ] Create implementation sprint prompt
