# GitHub Workflows

The Android workflows are present and use the repository-local Gradle wrapper.

Current workflows:

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

Both workflows also support `workflow_dispatch`; code/runtime checks run on push and pull requests according to their workflow definitions.
