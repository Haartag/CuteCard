# CuteCard

A language-learning flashcard component for **Compose Multiplatform** (Android + iOS).  
One component. Two exercise modes. Fully customisable. Zero hardcoded strings.

---

## Contents

- [Installation](#installation)
- [Quick start](#quick-start)
- [Interaction model](#interaction-model)
- [Exercise modes](#exercise-modes)
- [API reference](#api-reference)
  - [CuteCard](#flashcard-composable)
  - [CuteCardContent](#flashcardcontent)
  - [CuteCardConfig](#flashcardconfig)
  - [CuteCardStyle](#flashcardstyle)
  - [CuteCardLabels](#flashcardlabels)
  - [CuteCardDefaults](#flashcarddefaults)
  - [Enums](#enums)
- [Customisation](#customisation)
  - [Appearance](#appearance)
  - [Dark mode](#dark-mode)
  - [Animation](#animation)
  - [Labels and localisation](#labels-and-localisation)
- [Audio playback](#audio-playback)
- [Responsibility split](#responsibility-split)
- [Architecture overview](#architecture-overview)

---

## Installation

Add the library module to your Compose Multiplatform project:

```kotlin
// settings.gradle.kts
include(":cutecard")

// build.gradle.kts (shared module)
commonMain.dependencies {
    implementation(project(":cutecard"))
}
```

The library targets `androidTarget`, `iosArm64`, and `iosSimulatorArm64`.

---

## Quick start

```kotlin
CuteCard(
    content = CuteCardContent(
        word = "hierro",
        translation = "iron",
        phonetics = "[ˈje.ro]",
        wordClass = "noun",
        audioUrl = "https://cdn.example.com/audio/hierro.mp3"
    ),
    onKnown = { viewModel.markKnown() },
    onUnknown = { viewModel.markUnknown() },
    onAudioRequested = { player.play(content.audioUrl) }
)
```

That's it. Defaults handle everything else — animations, settled lock, visual style, labels.

---

## Interaction model

```
Front face
  │
  │  Tap card
  ▼
Back face (full info revealed)
  │                    │
  │  Tap card          │  Tap "I don't know"
  ▼                    ▼
onKnown()          onUnknown()
```

| Action | Result |
|---|---|
| Tap card (front) | Flips to back. Tap is locked for `settledLockDurationMs` to prevent accidental double-tap |
| Tap card (back, after lock) | Triggers confirm exit animation → calls `onKnown` |
| Tap dismiss button | Triggers dismiss exit animation → calls `onUnknown` |
| Tap audio button | Calls `onAudioRequested`. Does not flip the card |

**The positive action (I know it) is the path of least resistance.** The dismiss button is intentionally low-weight and below the card. The asymmetry is purposeful — it subtly encourages the learner without being manipulative.

---

## Exercise modes

The same component supports two distinct exercise types by changing one config value.

### Mode A — Word → Translation (default)

Front shows the word only. Back reveals the translation, phonetics, word class, and audio button.

```kotlin
CuteCard(
    content = content,
    config = CuteCardConfig(frontSide = CardFrontSide.Word), // default
    onKnown = { ... },
    onUnknown = { ... }
)
```

```
┌─────────────────────────┐
│                         │
│         hierro          │  ← tap to flip
│        [ˈje.ro]         │
│          noun           │
│                         │
└─────────────────────────┘
           ↓ flip
┌─────────────────────────┐
│                         │
│          iron           │
│        [ˈje.ro]         │
│          noun           │
│                         │
│  [ 🔊  Play word      ] │
└─────────────────────────┘

        I don't know
```

### Mode B — Translation → Word

Front shows full info (translation, phonetics, word class, audio). Back reveals the original word only. Useful for recall in the opposite direction.

```kotlin
CuteCard(
    content = content,
    config = CuteCardConfig(frontSide = CardFrontSide.Translation),
    onKnown = { ... },
    onUnknown = { ... }
)
```

The audio button always lives on the full-info face, regardless of which mode is active.

---

## API reference

### `CuteCard` composable

```kotlin
@Composable
fun CuteCard(
    content: CuteCardContent,
    onKnown: () -> Unit,
    onUnknown: () -> Unit,
    modifier: Modifier = Modifier,
    config: CuteCardConfig = CuteCardConfig(),
    style: CuteCardStyle = CuteCardDefaults.style(),
    labels: CuteCardLabels = CuteCardLabels(),
    isPlaying: Boolean = false,
    onAudioRequested: (() -> Unit)? = null,
    dismissButton: @Composable (onClick: () -> Unit) -> Unit = { /* built-in text button */ }
)
```

| Parameter | Type | Description |
|---|---|---|
| `content` | `CuteCardContent` | All data for this card. Changing this resets the card to front state |
| `onKnown` | `() -> Unit` | Called after confirm exit animation completes. Advance the deck here |
| `onUnknown` | `() -> Unit` | Called after dismiss exit animation completes. Re-queue or advance here |
| `modifier` | `Modifier` | Applied to the root layout |
| `config` | `CuteCardConfig` | Behavior and animation settings |
| `style` | `CuteCardStyle` | Visual appearance |
| `labels` | `CuteCardLabels` | All user-facing strings |
| `isPlaying` | `Boolean` | Drives audio button visual state. Owned by the consumer |
| `onAudioRequested` | `(() -> Unit)?` | Called on audio button tap. `null` hides the button entirely |
| `dismissButton` | `@Composable (onClick: () -> Unit) -> Unit` | Slot for a custom dismiss control. The `onClick` lambda **must be called** to trigger the dismiss animation. Defaults to the built-in text button |

---

### `CuteCardContent`

One instance per card. Build from any data source — database, API, hardcoded list.

```kotlin
data class CuteCardContent(
    val word: String,
    val translation: String,
    val phonetics: String? = null,   // null = hidden
    val wordClass: String? = null,   // null = hidden
    val audioUrl: String? = null     // null = audio button hidden
)
```

| Field | Required | Notes |
|---|---|---|
| `word` | Yes | Displayed on the word-only face |
| `translation` | Yes | Displayed on the full-info face |
| `phonetics` | No | e.g. `"[ˈje.ro]"`. Hidden when null — no empty space left behind |
| `wordClass` | No | e.g. `"noun"`, `"verb"`. Displayed in a small pill chip |
| `audioUrl` | No | The lib never reads this directly — it's your data to use in `onAudioRequested` |

---

### `CuteCardConfig`

Controls all behavior and animation. Visual appearance is controlled separately via `CuteCardStyle`.

```kotlin
data class CuteCardConfig(
    val frontSide: CardFrontSide = CardFrontSide.Word,
    val flipDurationMs: Int = 400,
    val settledLockDurationMs: Int = 350,
    val exitDurationMs: Int = 300,
    val flipDirection: FlipDirection = FlipDirection.Horizontal,
    val confirmExit: ExitAnimation = ExitAnimation.SlideUp,
    val dismissExit: ExitAnimation = ExitAnimation.SlideDown
)
```

| Field | Default | Description |
|---|---|---|
| `frontSide` | `Word` | Which face is shown first. See [Exercise modes](#exercise-modes) |
| `flipDurationMs` | `400` | Duration of the 3D flip animation in ms |
| `settledLockDurationMs` | `350` | How long taps are ignored after flipping. Prevents accidental double-tap |
| `exitDurationMs` | `300` | Duration of confirm / dismiss exit animation in ms |
| `flipDirection` | `Horizontal` | `Horizontal` = rotateY, `Vertical` = rotateX |
| `confirmExit` | `SlideUp` | Exit animation when card is marked as known |
| `dismissExit` | `SlideDown` | Exit animation when card is marked as unknown |

---

### `CuteCardStyle`

Controls the full visual appearance of the card. Build via `CuteCardDefaults.style()` and override with `copy()`.

```kotlin
data class CuteCardStyle(
    val cardShape: Shape,
    val cardElevation: Dp,
    val cardAspectRatio: Float,
    val cardBackgroundColor: Color,
    val ghostCardBackgroundColor: Color,
    val cardBorderColor: Color,
    val wordTextStyle: TextStyle,
    val wordTextColor: Color,
    val phoneticsTextStyle: TextStyle,
    val phoneticsTextColor: Color,
    val wordClassPillStyle: WordClassPillStyle,
    val audioButtonStyle: AudioButtonStyle,
    val dismissButtonStyle: DismissButtonStyle
)
```

#### `AudioButtonStyle`

```kotlin
data class AudioButtonStyle(
    val idleIconColor: Color,       // muted tone when not playing
    val playingIconColor: Color,    // accent color when playing
    val idleStrokeWidth: Dp,        // regular stroke weight
    val playingStrokeWidth: Dp,     // thicker stroke when playing
    val containerColor: Color,
    val contentColor: Color,
    val shape: Shape,
    val textStyle: TextStyle
)
```

The color and stroke width animate automatically between idle and playing states. Duration is controlled by the internal token `AudioButtonTransitionDurationMs` (200ms).

#### `DismissButtonStyle`

```kotlin
data class DismissButtonStyle(
    val contentColor: Color,
    val textStyle: TextStyle,
    val shape: Shape,
    val containerColor: Color = Color.Transparent
)
```

#### `WordClassPillStyle`

```kotlin
data class WordClassPillStyle(
    val textStyle: TextStyle,
    val textColor: Color,
    val containerColor: Color,
    val borderColor: Color,
    val shape: Shape
)
```

---

### `CuteCardLabels`

Every user-facing string. Nothing is hardcoded in the library.

```kotlin
data class CuteCardLabels(
    val dismissButtonLabel: String = "I don't know",
    val audioButtonIdleLabel: String = "Play word",
    val audioButtonPlayingLabel: String = "Playing...",
    val audioButtonContentDescription: String = "Play pronunciation",
    val cardFrontContentDescription: String = "Word card, tap to reveal translation",
    val cardBackContentDescription: String = "Translation revealed, tap to mark as known"
)
```

The content descriptions are read by TalkBack (Android) and VoiceOver (iOS). Tailor them to your app's voice.

---

### `CuteCardDefaults`

```kotlin
object CuteCardDefaults {
    fun config(): CuteCardConfig           // default config instance
    fun labels(): CuteCardLabels           // default labels instance
    @Composable fun style(): CuteCardStyle // default light mode style
    @Composable fun darkStyle(): CuteCardStyle // default dark mode style
}
```

---

### Enums

#### `CardFrontSide`
```kotlin
enum class CardFrontSide {
    Word,         // front = word only → back = full info + audio (default)
    Translation   // front = full info + audio → back = word only
}
```

#### `FlipDirection`
```kotlin
enum class FlipDirection {
    Horizontal,   // rotateY — card flips left to right (default)
    Vertical      // rotateX — card flips top to bottom
}
```

#### `ExitAnimation`
```kotlin
enum class ExitAnimation {
    SlideUp,      // card flies upward — use for confirmExit
    SlideDown,    // card drops below — use for dismissExit
    ScaleFade,    // card scales up then fades — use for confirmExit
    Fade,         // simple opacity fade — use for dismissExit
    None          // card disappears instantly, no animation
}
```

Valid pairings:

| Exit type | Recommended values |
|---|---|
| `confirmExit` | `SlideUp`, `ScaleFade`, `None` |
| `dismissExit` | `SlideDown`, `Fade`, `None` |

---

## Customisation

### Appearance

Use `CuteCardDefaults.style().copy(...)` to override only what you need:

```kotlin
CuteCard(
    content = content,
    onKnown = { ... },
    onUnknown = { ... },
    style = CuteCardDefaults.style().copy(
        cardBackgroundColor = Color(0xFFFFF8F0),
        cardShape = RoundedCornerShape(20.dp),
        wordTextColor = Color(0xFF2C2C2C)
    )
)
```

To customise the audio button:

```kotlin
style = CuteCardDefaults.style().copy(
    audioButtonStyle = CuteCardDefaults.style().audioButtonStyle.copy(
        playingIconColor = Color(0xFF4A90E2),
        playingStrokeWidth = 3.dp
    )
)
```

### Dark mode

A `darkStyle()` convenience function ships out of the box:

```kotlin
val isDark = isSystemInDarkTheme()

CuteCard(
    content = content,
    onKnown = { ... },
    onUnknown = { ... },
    style = if (isDark) CuteCardDefaults.darkStyle() else CuteCardDefaults.style()
)
```

Or build your own:

```kotlin
CuteCard(
    style = CuteCardDefaults.style().copy(
        cardBackgroundColor = Color(0xFF1E1E1E),
        cardBorderColor = Color(0x1AFFFFFF),
        wordTextColor = Color(0xFFF0EFE9),
        phoneticsTextColor = Color(0xFF888888)
    )
)
```

### Animation

```kotlin
CuteCard(
    content = content,
    onKnown = { ... },
    onUnknown = { ... },
    config = CuteCardConfig(
        flipDurationMs = 550,
        flipDirection = FlipDirection.Vertical,
        settledLockDurationMs = 500,
        confirmExit = ExitAnimation.ScaleFade,
        dismissExit = ExitAnimation.Fade,
        exitDurationMs = 400
    )
)
```

To disable exit animations entirely (instant disappear):

```kotlin
config = CuteCardConfig(
    confirmExit = ExitAnimation.None,
    dismissExit = ExitAnimation.None
)
```

### Labels and localisation

```kotlin
CuteCard(
    content = content,
    onKnown = { ... },
    onUnknown = { ... },
    labels = CuteCardLabels(
        dismissButtonLabel = stringResource(R.string.still_learning),
        audioButtonIdleLabel = stringResource(R.string.play_word),
        audioButtonPlayingLabel = stringResource(R.string.playing),
        cardFrontContentDescription = stringResource(R.string.card_front_desc),
        cardBackContentDescription = stringResource(R.string.card_back_desc)
    )
)
```

---

## Audio playback

The library handles the audio button UI and state — you handle playback.

```kotlin
// In your ViewModel or state holder
var isPlaying by mutableStateOf(false)
    private set

fun playAudio(url: String?) {
    if (url == null) return
    isPlaying = true
    player.play(url,
        onComplete = { isPlaying = false },
        onError = { isPlaying = false }
    )
}
```

```kotlin
// In your composable
CuteCard(
    content = content,
    onKnown = { viewModel.markKnown() },
    onUnknown = { viewModel.markUnknown() },
    isPlaying = viewModel.isPlaying,
    onAudioRequested = { viewModel.playAudio(content.audioUrl) }
)
```

**Auto-play on flip** — if your app should play audio automatically when the card flips, trigger `playAudio()` inside `onKnown`'s inverse or observe the card state. Since `onAudioRequested` is your callback, you can call it from anywhere.

**Hide the audio button** — pass `onAudioRequested = null`. No button is rendered and no empty space is left behind.

---

## Responsibility split

| Concern | Owner |
|---|---|
| Flip animation | Library |
| Exit animations | Library |
| Settled lock (accidental tap prevention) | Library |
| Audio idle / playing visual states | Library |
| Audio playback | Consumer |
| `isPlaying` state | Consumer |
| Card ordering / deck logic | Consumer |
| Data fetching | Consumer |
| Navigation between cards | Consumer |

---

## Architecture overview

```
/api                  ← everything the consumer touches (this document)
  CuteCard.kt
  CuteCardContent.kt
  CuteCardConfig.kt
  CuteCardStyle.kt
  CuteCardLabels.kt
  CuteCardDefaults.kt

/internal             ← never imported by the consumer
  /state
    CuteCardState.kt       ← sealed class: Front, Flipping, Settling, Back, ExitingConfirm, ExitingDismiss, Gone
    CuteCardStateHolder.kt ← state machine, settle timer flag
  /animation
    CuteCardAnimator.kt    ← AnimationSpec builders from config values
    FlipTransition.kt       ← 3D rotateY / rotateX logic
    ExitTransitions.kt      ← SlideUp, SlideDown, ScaleFade, Fade, None
  /ui
    CuteCardLayout.kt      ← wires everything together
    CardFront.kt
    CardBack.kt
    AudioButton.kt
    DismissButton.kt
    GhostStack.kt           ← two static cards behind the active card
  /theme
    CuteCardTokens.kt      ← internal constants (sizes, radii, durations)
```

The lib owns ephemeral UI state (flip progress, settle lock, exit animation). The consumer owns everything else. No responsibility crosses that boundary.
