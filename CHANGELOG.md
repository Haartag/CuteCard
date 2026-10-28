# Changelog

All notable changes to CuteCard are documented here. The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project uses [Semantic Versioning](https://semver.org/). Before 1.0.0, minor versions may contain breaking changes.

## [Unreleased]

## [0.5.1] - 2026-10-28

### Fixed

- `DismissButtonStyle.containerColor` is now drawn as the built-in dismiss button's background, clipped to its `shape`. The default stays transparent, so the default look is unchanged.

## [0.5.0] - 2026-10-28

### Breaking changes

- **The unflip button is off unless you place it.** `unflipButton` is no longer nullable, and the button only appears when `CuteCardConfig.unflipButtonPosition` is `AboveCard` or `CardTopEnd` (default `None`).
- **`CuteCardStyle.cardElevation` is removed.** Shadows are now described with `CuteCardStyle.shadows`.
- **`CuteCard` has a new `state` parameter** directly after `modifier`. Only calls that pass later arguments by position are affected.
- **The default top-card shadow looks slightly different on Android.** It is now a two-layer shadow drawn the same way on Android and iOS, instead of the platform elevation shadow.

### Migrating from 0.4.0

| If your code… | Change it to… |
|---|---|
| Passes a custom `unflipButton = { onClick -> ... }` | Keep the slot and add `config = CuteCardConfig(unflipButtonPosition = UnflipButtonPosition.AboveCard)` |
| Passes `unflipButton = null` | Remove the argument — the button is off by default |
| Sets `cardElevation = …` | Remove it. To change the shadow, set `shadows = CardShadows(...)` |
| Passes `config`, `style` or later arguments by position | Use named arguments |

### Added

- **Optional dismiss button.** Pass `dismissButton = null` to remove the "I don't know" button and its reserved space. `onUnknown` now defaults to a no-op, so it can be omitted.
- **Dismiss from your own controls.** `rememberCuteCardState()` returns a `CuteCardState` with `dismiss()` and an observable `canDismiss`, so an "I don't know" control can live anywhere in your UI. `dismiss()` works from both faces and respects `cardActionsEnabled`.
- **Unflip button positions.** `UnflipButtonPosition.AboveCard` or `CardTopEnd` (inside the card's top-right corner, rotating with the back face). The built-in rewind icon is now the default slot content.
- **Custom audio button.** The `audioButton` slot receives `isPlaying` and an `onClick` that forwards to `onAudioRequested`.
- **Word placement.** `CuteCardStyle.contentPadding`, `reservedTopHeight`, `reservedBottomHeight` and `wordVerticalPosition` (`Raised` or `Centered`). Defaults match the previous layout.
- **Back-face word style.** `CuteCardStyle.backWordTextStyle` (merged over `wordTextStyle`) and `backWordTextColor`.
- **Ghost stack geometry and opacity.** `CuteCardStyle.ghostStackStyle` with `sideInsetPerStep`, `offsetPerStep`, `nearCardAlpha` and `farCardAlpha`. Defaults match the previous look.
- **Layered shadows.** `CardShadow` (color, blur, offsetY, optional spread) and `CardShadows` with separate layer lists for `topOverDeck`, `topAlone`, `nearDeckCard` and `farDeckCard`. Unset deck lists keep the previous deck shadows exactly.

### Changed

- The README now covers installation, quick start and key features. The full API reference lives in `documentation/CuteCard_Documentation.md`.

## Earlier versions

Changes before 0.5.0 are recorded in the git history.

[Unreleased]: https://github.com/Haartag/CuteCard/compare/v0.5.1...HEAD
[0.5.1]: https://github.com/Haartag/CuteCard/compare/v0.5.0...v0.5.1
[0.5.0]: https://github.com/Haartag/CuteCard/compare/v0.4.0...v0.5.0
