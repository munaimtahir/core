# Codex / AI Agent Prompt Starter

Use this prompt when starting a new coding session.

```text
You are working in a brand-new Android launcher foundation repository named `core`.

This is a greenfield project. Do not reuse legacy code, architecture, workflows, UI flows, or state systems from any existing project.

First read:
- README.md
- docs/PROJECT_CONTEXT.md
- docs/GREENFIELD_POLICY.md
- docs/BASELINE_SCOPE.md
- docs/PRODUCT_GUARDRAILS.md
- docs/LAUNCHER_CUSTOMIZATION_SCOPE.md
- docs/ARCHITECTURE.md
- docs/TESTING/TESTING_STRATEGY.md
- docs/DEFINITION_OF_DONE.md
- AGENTS.md
- GEMINI.md
- .github/copilot-instructions.md

Before coding, create or update copilot_session.md with:
- current goal
- repo state
- implementation plan
- task checklist
- files inspected
- commands to run

Current goal: work only on the baseline launcher foundation.

Allowed baseline work:
- launcher skeleton
- launcher intent
- home screen
- app scanner
- app icons
- app labels
- app launching
- app list
- fixed home grid
- local settings
- basic theme persistence
- tests
- CI
- documentation

Forbidden baseline work:
- caregiver PIN
- hidden access
- layout lock
- app hiding
- parent mode
- child mode
- school mode
- office mode
- remote sync
- cloud account
- premium logic
- ads
- subscriptions
- kiosk/device-owner features
- Android system UI replacement
- real notification shade replacement
- real Quick Settings replacement
- restricted Wi-Fi/mobile-data control

Run real verification commands. Do not claim success unless build/tests/lint actually pass.

End with:
- summary
- files changed
- commands run
- verification results
- remaining issues
- final verdict: GO / Conditional GO / NO-GO
```
