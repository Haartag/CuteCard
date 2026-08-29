package site.llinsoft.cutecard.internal.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Drives the [CuteCardState] machine for a single [CuteCard] instance.
 *
 * Created once via `remember { CuteCardStateHolder() }` inside the
 * public [CuteCard] composable. Scoped to the composable — destroyed when
 * the card leaves composition.
 *
 * All state transitions are validated — invalid transitions are silently ignored.
 */
@Stable
internal class CuteCardStateHolder {

    /**
     * Current state of the card. Observed by the composable to drive
     * animation and interaction.
     */
    var state: CuteCardState by mutableStateOf(CuteCardState.Front)
        private set

    /**
     * True while the card is in [CuteCardState.Settling].
     * The composable watches this and runs the settle timer [LaunchedEffect].
     */
    val shouldRunSettleTimer: Boolean
        get() = state is CuteCardState.Settling

    // Transition API

    /**
     * Called when the user taps the card.
     * Valid from: [CuteCardState.Front] → starts flip.
     * Valid from: [CuteCardState.Back] → starts confirm exit.
     * All other states: ignored (interaction is locked).
     */
    fun onCardTap() {
        when (state) {
            CuteCardState.Front -> transitionTo(CuteCardState.Flipping)
            CuteCardState.Back  -> transitionTo(CuteCardState.ExitingConfirm)
            else                 -> Unit
        }
    }

    /**
     * Called when the dismiss button is tapped.
     * Valid from: [CuteCardState.Back] only.
     * All other states: ignored.
     */
    fun onDismissTap() {
        if (state is CuteCardState.Back) {
            transitionTo(CuteCardState.ExitingDismiss)
        }
    }

    /**
     * Called when the unflip button is tapped.
     * Valid from: [CuteCardState.Back] only — starts the reverse flip animation.
     * All other states: ignored.
     */
    fun onUnflipTap() {
        if (state is CuteCardState.Back) {
            transitionTo(CuteCardState.UnFlipping)
        }
    }

    // Code-driven transitions
    //
    // Separate from the tap handlers above because a tap on the back face means
    // "I know it" and starts the confirm exit. Code asking for a flip never means that.

    /**
     * Flips the card from code, as a tap on the front face would.
     * Valid from: [CuteCardState.Front] only. All other states: ignored.
     */
    fun requestFlip() {
        if (state is CuteCardState.Front) {
            transitionTo(CuteCardState.Flipping)
        }
    }

    /**
     * Reverse-flips the card from code, as the unflip button would.
     * Valid from: [CuteCardState.Back] only. All other states: ignored.
     */
    fun requestUnflip() {
        if (state is CuteCardState.Back) {
            transitionTo(CuteCardState.UnFlipping)
        }
    }

    /**
     * Called by the composable when the reverse flip animation finishes.
     * Moves from [CuteCardState.UnFlipping] → [CuteCardState.Front].
     */
    fun onUnflipAnimationFinished() {
        if (state is CuteCardState.UnFlipping) {
            transitionTo(CuteCardState.Front)
        }
    }

    /**
     * Called by the composable when the flip animation finishes.
     * Moves from [CuteCardState.Flipping] → [CuteCardState.Settling].
     */
    fun onFlipAnimationFinished() {
        if (state is CuteCardState.Flipping) {
            transitionTo(CuteCardState.Settling)
        }
    }

    /**
     * Called by the composable's [LaunchedEffect] after
     * [CuteCardConfig.settledLockDurationMs] elapses.
     * Moves from [CuteCardState.Settling] → [CuteCardState.Back].
     */
    fun onSettled() {
        if (state is CuteCardState.Settling) {
            transitionTo(CuteCardState.Back)
        }
    }

    /**
     * Called by the composable when the exit animation finishes.
     * Moves from [CuteCardState.ExitingConfirm] or [CuteCardState.ExitingDismiss]
     * → [CuteCardState.Gone].
     */
    fun onExitAnimationFinished() {
        when (state) {
            CuteCardState.ExitingConfirm,
            CuteCardState.ExitingDismiss -> transitionTo(CuteCardState.Gone)
            else                          -> Unit
        }
    }

    /**
     * Resets the state machine to [CuteCardState.Front].
     * Called when [CuteCardContent] changes — i.e. a new card is shown.
     */
    fun reset() {
        transitionTo(CuteCardState.Front)
    }

    // Internal

    private fun transitionTo(next: CuteCardState) {
        state = next
    }
}
