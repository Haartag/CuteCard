# CuteCard

[![Version](https://img.shields.io/badge/version-0.5.0-blue)](https://github.com/Haartag/CuteCard/releases/tag/v0.5.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A language-learning flashcard component for **Compose Multiplatform** (Android & iOS).

CuteCard handles the full interaction lifecycle of a single flashcard - 3D flip animation, settled-lock delay, confirm/dismiss exit animations, and audio button visuals - so you can focus on your deck logic and data.

---

## Features

- 3D card flip (horizontal or vertical axis)
- Two exercise modes — see the word, recall the translation, or vice versa
- Confirm and dismiss exit animations (slide, scale-fade, fade)
- Audio button with idle/playing visual states, or your own custom button
- "I don't know" button — built-in, custom, hidden, or your own control placed anywhere
- Unflip button — flip back to the front face without dismissing, above the card or in its corner
- Programmatic flip — drive the card from code for onboarding and tutorials
- Ghost card stack for a physical deck feel, with configurable inset, offset and opacity
- Layered shadows — several shadows per card, separate for the top card and each deck card
- Configurable word placement and per-side word text style
- Language indicator pill — small 2-3 character code (e.g. "EN", "ES") in the card corner
- Fully customizable colors, shapes, typography, labels and timing, with a built-in dark style

---

## Installation

```kotlin
implementation("site.llinsoft:cutecard:0.5.0")
```

---

## Quick start

```kotlin
var isPlaying by remember { mutableStateOf(false) }

CuteCard(
    content = CuteCardContent(
        word = "hierro",
        translation = "iron",
        phonetics = "[ˈje.ro]",
        wordClass = "noun",
        audioUrl = "https://example.com/hierro.mp3"
    ),
    onKnown = { /* advance to next card */ },
    onUnknown = { /* re-queue or move on */ },
    isPlaying = isPlaying,
    onAudioRequested = {
        isPlaying = true
        player.play(content.audioUrl) { isPlaying = false }
    }
)
```

- Tap the card to flip it, tap the back to mark it as known, or tap "I don't know".
- `onKnown` and `onUnknown` are called **after** their exit animations complete — replace the card there.
- Playback is yours: the library only draws the audio button and calls `onAudioRequested`. Pass `null` to hide the button.

---

## Key features

### Exercise modes

```kotlin
config = CuteCardConfig(frontSide = CardFrontSide.Translation)
```

`Word` (default) shows the word first and reveals translation, phonetics, word class and audio. `Translation` reverses it.

### Styling and dark mode

```kotlin
style = CuteCardDefaults.style().copy(
    cardBackgroundColor = Color(0xFFFFF8F0),
    wordTextColor = Color(0xFF2C2C2C)
)

style = CuteCardDefaults.darkStyle()
```

Start from the defaults and override only what you need.

### "I don't know" button

```kotlin
// Hide it
CuteCard(content = content, onKnown = { ... }, dismissButton = null)

// Or place your own control anywhere
val cardState = rememberCuteCardState()

CuteCard(content = content, state = cardState, dismissButton = null, onKnown = { ... }, onUnknown = { ... })

Button(onClick = { cardState.dismiss() }, enabled = cardState.canDismiss) { Text("I don't know") }
```

### Unflip button

```kotlin
config = CuteCardConfig(unflipButtonPosition = UnflipButtonPosition.CardTopEnd)
```

Off by default. `AboveCard` or `CardTopEnd` shows a rewind button that flips the card back without dismissing it.

---

## Documentation

Upgrading? See the [changelog](CHANGELOG.md) for breaking changes and migration notes.

Everything else is in the [full documentation](documentation/CuteCard_Documentation.md):

| Topic | Section |
|---|---|
| All parameters, data classes and enums | [API reference](documentation/CuteCard_Documentation.md#api-reference) |
| Word placement — padding, reserved rows, centring | [Word placement](documentation/CuteCard_Documentation.md#word-placement) |
| Layered shadows | [Shadows](documentation/CuteCard_Documentation.md#shadows) |
| Ghost stack size, offset, opacity | [Ghost stack](documentation/CuteCard_Documentation.md#ghost-stack) |
| Per-side word text style and pill colors | [Per-side word text style](documentation/CuteCard_Documentation.md#per-side-word-text-style), [LanguagePillStyle](documentation/CuteCard_Documentation.md#languagepillstyle) |
| Custom audio, dismiss and unflip buttons | [Audio playback](documentation/CuteCard_Documentation.md#audio-playback), [Dismiss button](documentation/CuteCard_Documentation.md#dismiss-button), [Unflip button](documentation/CuteCard_Documentation.md#unflip-button) |
| Flip callbacks | [Flip callbacks](documentation/CuteCard_Documentation.md#flip-callbacks) |
| Programmatic flip for tutorials | [Programmatic flip](documentation/CuteCard_Documentation.md#programmatic-flip) |
| Animation timing and exit animations | [Animation](documentation/CuteCard_Documentation.md#animation) |
| Labels and localisation | [Labels and localisation](documentation/CuteCard_Documentation.md#labels-and-localisation) |

---

## License

MIT — see [LICENSE](LICENSE) for details.

---

## Building and testing

```bash
# Build all targets
./gradlew :cutecard:build

# Run common tests
./gradlew :cutecard:allTests

# Build Android artifact
./gradlew :cutecard:assemble

# Build iOS frameworks
./gradlew :cutecard:linkDebugFrameworkIosArm64 \
          :cutecard:linkDebugFrameworkIosSimulatorArm64
```
