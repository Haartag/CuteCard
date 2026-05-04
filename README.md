# CuteCard

[![Version](https://img.shields.io/badge/version-0.2.4-blue)](https://github.com/llin-pixel/CuteCard/releases/tag/v0.2.4)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A language-learning flashcard component for **Compose Multiplatform** (Android & iOS).

CuteCard handles the full interaction lifecycle of a single flashcard - 3D flip animation, settled-lock delay, confirm/dismiss exit animations, and audio button visuals - so you can focus on your deck logic and data.

---

## Features

- 3D card flip (horizontal or vertical axis)
- Confirm and dismiss exit animations (slide, scale-fade, fade)
- Two exercise modes — see the word, recall the translation, or vice versa
- Ghost card stack for a physical deck feel
- Audio button with idle/playing visual states
- Fully customizable: colors, shapes, typography, labels, timing
- Built-in dark mode style

---

## Installation

```kotlin
implementation("site.llinsoft:cutecard:0.2.4")
```

For the full API reference see [documentation/CuteCard_Documentation.md](documentation/CuteCard_Documentation.md).

---

## Quick start

```kotlin
CuteCard(
    content = CuteCardContent(
        word = "iron",
        translation = "hierro",
        phonetics = "[ˈje.ro]",
        wordClass = "noun",
        audioUrl = "https://example.com/hierro.mp3"
    ),
    onKnown = { /* advance to next card */ },
    onUnknown = { /* re-queue or move on */ },
    onAudioRequested = { player.play(content.audioUrl) }
)
```

`onKnown` and `onUnknown` are called **after** their respective exit animations complete — replace the card there.

---

## Content

`CuteCardContent` holds all data for a single card. One instance = one card.

```kotlin
CuteCardContent(
    word = "correr",           // required — the word to learn
    translation = "to run",    // required — the translation
    phonetics = "[ko.ˈrer]",  // optional — hides phonetics row when null
    wordClass = "verb",        // optional — hides word class pill when null
    audioUrl = "https://..."   // optional — hides audio button when null
)
```

Changing `content` while the component is in composition resets the card to its front face.

---

## Exercise modes

Control which face appears first via `CuteCardConfig.frontSide`.

| Mode | Front face | Back face |
|---|---|---|
| `CardFrontSide.Word` *(default)* | Word only | Translation + phonetics + audio |
| `CardFrontSide.Translation` | Translation + phonetics + audio | Word only |

```kotlin
// Mode B — see the full info, recall the word
CuteCard(
    content = content,
    onKnown = { ... },
    onUnknown = { ... },
    config = CuteCardConfig(frontSide = CardFrontSide.Translation)
)
```

---

## Configuration

`CuteCardConfig` controls all behavior and animation. All fields have sensible defaults.

```kotlin
CuteCardConfig(
    frontSide = CardFrontSide.Word,          // which face shows first
    flipDurationMs = 400,                    // 3D flip duration
    settledLockDurationMs = 350,             // tap-lock after flip (prevents accidental confirm)
    exitDurationMs = 300,                    // exit animation duration
    flipDirection = FlipDirection.Horizontal, // or Vertical
    confirmExit = ExitAnimation.SlideUp,     // animation when marked as known
    dismissExit = ExitAnimation.SlideDown,   // animation when marked as unknown
)
```

### Exit animations

| Value | Effect |
|---|---|
| `SlideUp` | Card slides up and fades out |
| `SlideDown` | Card slides down and fades out |
| `ScaleFade` | Card scales up slightly then fades out |
| `Fade` | Card fades out in place |
| `None` | Card disappears instantly (no animation) |

`confirmExit` and `dismissExit` are independent. Use `None` to disable animation.

---

## Styling

Build from `CuteCardDefaults.style()` and override only what you need.

```kotlin
CuteCard(
    content = content,
    onKnown = { ... },
    onUnknown = { ... },
    style = CuteCardDefaults.style().copy(
        cardBackgroundColor = Color(0xFF1E1E1E),
        wordTextColor = Color(0xFFF0EFE9)
    )
)
```

### Dark mode

A pre-built dark style ships out of the box:

```kotlin
style = CuteCardDefaults.darkStyle()
```

### Style reference

`CuteCardStyle` exposes:

| Field | Controls |
|---|---|
| `cardShape` | Card corner shape |
| `cardElevation` | Shadow depth of the active card |
| `cardAspectRatio` | Width-to-height ratio (default 3:4) |
| `cardBackgroundColor` | Card surface fill |
| `ghostCardBackgroundColor` | Stack cards behind the active card |
| `cardBorderColor` | Optional card stroke (transparent = none) |
| `wordTextStyle` / `wordTextColor` | Primary word / translation typography |
| `phoneticsTextStyle` / `phoneticsTextColor` | Phonetics row typography |
| `wordClassPillStyle` | Word class chip — text, colors, shape |
| `audioButtonStyle` | Audio button — icon colors, stroke widths, shape, typography |
| `dismissButtonStyle` | Dismiss button — text color, typography, shape |

---

## Labels and localization

`CuteCardLabels` collects all user-facing strings in one place. Pass it to override the language or wording — defaults are English.

**Visible text**
- `dismissButtonLabel` — text on the default "I don't know" button.
- `audioButtonIdleLabel` / `audioButtonPlayingLabel` — text shown on the audio button in its idle and playing states.

**Accessibility labels** (screen readers only, never shown visually)
- `audioButtonContentDescription`, `cardFrontContentDescription`, `cardBackContentDescription`.

```kotlin
CuteCard(
    content = content,
    onKnown = { ... },
    onUnknown = { ... },
    labels = CuteCardLabels(
        dismissButtonLabel = "Noch nicht",
        audioButtonIdleLabel = "Anhören",
        audioButtonPlayingLabel = "Läuft...",
        audioButtonContentDescription = "Aussprache abspielen",
        cardFrontContentDescription = "Wortkarte, tippen zum Aufdecken",
        cardBackContentDescription = "Übersetzung sichtbar, tippen zum Bestätigen"
    )
)
```

---

## Audio button

The audio button appears on the full-info face when `onAudioRequested` is non-null. The library manages visuals only — playback is entirely the consumer's responsibility.

```kotlin
var isPlaying by remember { mutableStateOf(false) }

CuteCard(
    content = content,
    onKnown = { ... },
    onUnknown = { ... },
    isPlaying = isPlaying,
    onAudioRequested = {
        isPlaying = true
        player.play(content.audioUrl) {
            isPlaying = false  // called when playback ends
        }
    }
)
```

When `isPlaying` is true the button switches to an accent color and thicker stroke. When `onAudioRequested` is `null`, the button is hidden entirely with no empty space left behind.

---

## Custom dismiss button

The dismiss button slot accepts any composable. The `onClick` lambda passed to it **must be called** to trigger the dismiss animation.

```kotlin
// Icon button
CuteCard(
    content = content,
    onKnown = { ... },
    onUnknown = { ... },
    dismissButton = { onClick ->
        IconButton(onClick = onClick) {
            Icon(Icons.Default.Close, contentDescription = "I don't know")
        }
    }
)

// Image button
CuteCard(
    content = content,
    onKnown = { ... },
    onUnknown = { ... },
    dismissButton = { onClick ->
        Image(
            painter = painterResource(Res.drawable.thumbs_down),
            contentDescription = "I don't know",
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
)
```

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
