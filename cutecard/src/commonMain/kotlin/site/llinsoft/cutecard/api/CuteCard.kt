package site.llinsoft.cutecard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import site.llinsoft.cutecard.internal.state.CuteCardStateHolder
import site.llinsoft.cutecard.internal.ui.CuteCardLayout
import site.llinsoft.cutecard.internal.ui.DismissButton
import site.llinsoft.cutecard.internal.ui.UnflipButton

/**
 * A language-learning flashcard component for Compose Multiplatform.
 *
 * Displays a single word card that the user can flip to reveal its translation,
 * then mark as known or unknown. Supports two exercise directions, optional
 * phonetics, word class, and audio playback.
 *
 * ---
 *
 * **Basic usage:**
 * ```kotlin
 * CuteCard(
 *     content = CuteCardContent(
 *         word = "hierro",
 *         translation = "iron",
 *         phonetics = "[ˈje.ro]",
 *         wordClass = "noun",
 *         audioUrl = "https://..."
 *     ),
 *     onKnown = { viewModel.markKnown() },
 *     onUnknown = { viewModel.markUnknown() },
 *     onAudioRequested = { player.play(it) }
 * )
 * ```
 *
 * ---
 *
 * **Interaction model:**
 * - Tap card (front) → flip to back
 * - Tap card (back, after settle delay) → mark as known → [onKnown]
 * - Tap dismiss button → mark as unknown → [onUnknown]
 * - Tap audio button → [onAudioRequested] (consumer handles playback)
 * - Set [forceFlipped] → flip the card from code (onboarding / tutorials)
 *
 * @param content All data for this card. One instance = one card.
 * Changing this value resets the card to its front state.
 * @param onKnown Called after the confirm exit animation completes.
 * Advance to the next card here.
 * @param onUnknown Called after the dismiss exit animation completes.
 * Re-queue the card or advance here.
 * @param config Behavior and animation settings. See [CuteCardConfig].
 * @param style Visual appearance. See [CuteCardStyle] and [CuteCardDefaults].
 * @param labels All user-facing strings. See [CuteCardLabels].
 * @param isPlaying Whether audio is currently playing.
 * Drives the audio button's visual state.
 * Owned and updated by the consumer.
 * @param remainingCards Number of cards left in the deck including this one.
 * When `null` (default) the ghost stack always shows two cards.
 * Pass the actual count to have the stack shrink on the last two cards:
 * 2 cards remaining → one ghost card, 1 card remaining → no ghost cards.
 * @param forceFlipped Flips the card from code instead of waiting for a tap.
 * `null` (default) leaves the card entirely user-driven.
 * `true` shows the back face, `false` shows the front face.
 * This is a target, not a one-shot event: a value set while the
 * card is mid-animation is applied as soon as the card is
 * eligible, so callers never have to time their requests around
 * [CuteCardConfig.flipDurationMs] or
 * [CuteCardConfig.settledLockDurationMs].
 * Intended for onboarding and tutorial demonstrations.
 * @param cardActionsEnabled Whether the user can drive the card. When `false`, tap-to-flip,
 * tap-to-confirm, the dismiss button and the unflip button are all
 * inert — the buttons stay visible so a tutorial can point at them.
 * **Audio playback is unaffected** and the audio button keeps
 * working; this gates only the actions that advance the card.
 * Defaults to `forceFlipped == null`, so driving the card from
 * code locks the user out automatically. Pass `true` explicitly
 * to allow both at once.
 * @param onAudioRequested Called when the audio button is tapped.
 * The consumer is responsible for all audio playback.
 * `null` hides the audio button entirely.
 * @param onFlipped Called when the card flip is fully complete and the back face
 * is interactive (after the flip animation and settle lock).
 * Use this to auto-play audio or update analytics.
 * `null` means no callback.
 * @param onFlippedBack Called when the user taps the back face to confirm the card
 * as known (before the exit animation starts).
 * Fires at the same moment as [onKnown] is eventually
 * triggered, but earlier in the lifecycle — useful for
 * immediate UI reactions.
 * `null` means no callback.
 * @param dismissButton Composable slot for the "I don't know" control shown below the card.
 * Receives an `onClick` lambda that must be called to
 * trigger the dismiss animation. Defaults to the
 * built-in text button. Use this to supply a custom
 * icon, image, or any other composable:
 * ```kotlin
 *     dismissButton = { onClick ->
 *         IconButton(onClick = onClick) {
 *             Icon(Icons.Default.Close, contentDescription = "I don't know")
 *         }
 *     }
 * ```
 * @param unflipButton Composable slot for an icon shown above the card (top-right) while the
 * back face is visible. Receives an `onClick` lambda that must be called
 * to trigger the reverse flip animation back to the front face.
 * `null` (default) disables the button entirely — no space is reserved.
 * Supply any composable to enable it:
 * ```kotlin
 *     unflipButton = { onClick ->
 *         IconButton(onClick = onClick) {
 *             Icon(Icons.Default.Replay, contentDescription = "Show word")
 *         }
 *     }
 * ```
 */
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
    remainingCards: Int? = null,
    forceFlipped: Boolean? = null,
    cardActionsEnabled: Boolean = (forceFlipped == null),
    onAudioRequested: (() -> Unit)? = null,
    onFlipped: (() -> Unit)? = null,
    onFlippedBack: (() -> Unit)? = null,
    dismissButton: @Composable (onClick: () -> Unit) -> Unit = { onClick ->
        DismissButton(onClick = onClick, style = style.dismissButtonStyle, labels = labels)
    },
    unflipButton: (@Composable (onClick: () -> Unit) -> Unit)? = null
) {
    val stateHolder = remember { CuteCardStateHolder() }

    LaunchedEffect(content) {
        stateHolder.reset()
    }

    CuteCardLayout(
        stateHolder = stateHolder,
        content = content,
        config = config,
        style = style,
        labels = labels,
        isPlaying = isPlaying,
        remainingCards = remainingCards,
        forceFlipped = forceFlipped,
        cardActionsEnabled = cardActionsEnabled,
        onAudioRequested = onAudioRequested,
        onFlipped = onFlipped,
        onFlippedBack = onFlippedBack,
        onKnown = onKnown,
        onUnknown = onUnknown,
        dismissButton = dismissButton,
        unflipButton = unflipButton,
        modifier = modifier
    )
}
