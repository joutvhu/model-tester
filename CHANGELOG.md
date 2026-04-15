# Changelog

All notable changes to this project will be documented in this file.

## [1.1.0] - 2026-04-15

### Added
- Comprehensive JavaDocs to the core API for better developer experience.
- Support for modern Java models and custom naming strategies.
- Deep equality checks and improved validation logic.
- Phase 4: Integration improvements, documentation, and final cleanup.
- Revamped README with professional usage guides and practical examples.
- `CODE_OF_CONDUCT.md` for project governance.

### Changed
- Infrastructure enhancements to improve core performance and maintainability.
- Updated compatibility for JDK 21.
- Refactored model classes and test methods to be more descriptive and consistent.
- Switched to Slack-style logging using `@Slf4j`.

## [1.0.5] - 2023-03-20

### Added
- Support for backing up and restoring enum values during tests.
- Improved handling of nullable parameters (allowing `null`).

## [1.0.4] - 2023-03-15

### Added
- Initial support for testing enum types.

## [1.0.3] - 2023-03-15

### Added
- Explicit validation for `equals(null)` behavior.

## [1.0.2] - 2023-03-15

### Added
- Support for testing getters and setters even when a corresponding field is missing.

## [1.0.1] - 2023-03-14

### Fixed
- Minor bug fixes and refinement during initial release phase.

## [1.0.0] - 2023-03-14

### Added
- Initial release of the Model Tester library.
- Basic support for automated getter, setter, and constructor testing.
- Integration with JUnit 5.
