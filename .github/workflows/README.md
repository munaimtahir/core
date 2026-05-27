# GitHub Workflows

Workflows will be added after the Android project skeleton exists.

Trigger policy:

- All workflows must be manual only using `workflow_dispatch`.
- Do not add `push`, `pull_request`, or `pull_request_target` triggers.
- Workflows must not auto-run on commit or PR events.

Planned workflows:

1. Android Code CI
   - build
   - unit tests
   - lint

2. Android Runtime Emulator CI
   - boot emulator
   - install APK
   - launch app
   - capture screenshot
   - collect logcat
   - upload artifacts

Do not add workflow files until the Android project structure and Gradle commands are known.
