# Copilot Session Handoff

## Project

```text
core
```

## Current goal

Initialize a brand-new Android launcher foundation project.

## Current status

Documentation pack prepared.

No Android project skeleton has been created yet.

## Active principle

Build the stable baseline first.

Do not add product-specific variants until the launcher foundation is verified.

## Greenfield reminder

Do not reuse legacy code, architecture, workflows, UI flows, or state systems from any existing project.

## Task checklist

- [ ] Create new GitHub repository named `core`
- [ ] Add documentation pack
- [ ] Commit documentation as first commit
- [ ] Create clean Android project skeleton
- [ ] Configure launcher/home intent
- [ ] Add minimal home screen
- [ ] Add initial build workflow
- [ ] Verify build
- [ ] Verify unit tests
- [ ] Verify lint
- [ ] Add emulator runtime workflow
- [ ] Create baseline verification report

## Files inspected

None yet.

## Files changed

Documentation pack only.

## Commands run

None yet.

## Verification results

Not yet applicable.

## Remaining issues

Android project has not yet been created.

## Next recommended step

Create the GitHub repository named `core`, add this documentation pack, and commit it before coding begins.


---

## Documentation update — launcher customization scope

## Goal

Add the finalized launcher customization boundary to the `core` documentation pack.

## Files changed

- `docs/LAUNCHER_CUSTOMIZATION_SCOPE.md` added
- `README.md` updated
- `docs/PROJECT_CONTEXT.md` updated
- `docs/PRODUCT_GUARDRAILS.md` updated
- `docs/ROADMAP.md` updated
- `TASKS.md` updated
- `AGENTS.md` updated
- `GEMINI.md` updated
- `CODEX_PROMPT_STARTER.md` updated

## Documentation updates completed

- Baseline v0.1 features are clearly separated from post-baseline customization.
- Product variant features are separated from generic `core` foundation.
- Android system UI boundaries are documented.
- Status bar, notification shade, Quick Settings, restricted toggles, and full lockdown claims are explicitly excluded from normal launcher scope.

## Remaining implementation work

No Android implementation work has been performed yet.

Next implementation stage remains: create a clean Android project skeleton and configure launcher/home intent.

## Final verdict

GO for documentation update.
