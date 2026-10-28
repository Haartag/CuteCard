package site.llinsoft.cutecard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import site.llinsoft.cutecard.internal.state.CuteCardStateHolder

/**
 * Hoisted state for [CuteCard]. Lets controls placed anywhere in the consumer's
 * UI drive the card.
 *
 * Create it with [rememberCuteCardState] and pass it to [CuteCard]. One state
 * belongs to one [CuteCard] — it resets itself whenever the card's content changes,
 * so it can be kept for the whole deck.
 *
 * ```kotlin
 * val cardState = rememberCuteCardState()
 *
 * CuteCard(
 *     content = card,
 *     state = cardState,
 *     dismissButton = null,
 *     onKnown = { ... },
 *     onUnknown = { ... }
 * )
 *
 * Button(
 *     onClick = { cardState.dismiss() },
 *     enabled = cardState.canDismiss
 * ) { Text("I don't know") }
 * ```
 */
@Stable
class CuteCardState internal constructor() {

    internal val holder = CuteCardStateHolder()

    internal var actionsEnabled by mutableStateOf(true)

    /**
     * Whether [dismiss] would currently take effect: the card is resting on either
     * face and [CuteCard]'s `cardActionsEnabled` is `true`. `false` while the card
     * is flipping, settling or exiting. Observable — use it to enable or hide a
     * custom dismiss control.
     */
    val canDismiss: Boolean
        get() = actionsEnabled && holder.canRequestDismiss

    /**
     * Marks the card as unknown, exactly as the built-in dismiss button does:
     * plays [CuteCardConfig.dismissExit], then calls `onUnknown`.
     *
     * Works from both faces. Ignored when [canDismiss] is `false`.
     */
    fun dismiss() {
        if (actionsEnabled) holder.requestDismiss()
    }
}

/** Creates and remembers a [CuteCardState]. */
@Composable
fun rememberCuteCardState(): CuteCardState = remember { CuteCardState() }
