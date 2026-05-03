package site.llinsoft.cutecard.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import site.llinsoft.cutecard.internal.state.CuteCardStateHolder
import site.llinsoft.cutecard.internal.ui.CuteCardLayout
import site.llinsoft.cutecard.internal.ui.DismissButton

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
 * @param onAudioRequested Called when the audio button is tapped.
 * The consumer is responsible for all audio playback.
 * `null` hides the audio button entirely.
 * @param dismissButton Composable slot for the "I don't know" control.
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
    onAudioRequested: (() -> Unit)? = null,
    dismissButton: @Composable (onClick: () -> Unit) -> Unit = { onClick ->
        DismissButton(onClick = onClick, style = style.dismissButtonStyle, labels = labels)
    }
) {
    val stateHolder = remember { CuteCardStateHolder(config) }

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
        onAudioRequested = onAudioRequested,
        onKnown = onKnown,
        onUnknown = onUnknown,
        dismissButton = dismissButton,
        modifier = modifier
    )
}
