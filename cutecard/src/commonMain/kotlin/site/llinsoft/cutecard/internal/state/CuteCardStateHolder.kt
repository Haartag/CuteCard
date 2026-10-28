package site.llinsoft.cutecard.internal.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Drives the [CuteCardPhase] machine for a single [CuteCard] instance.
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
    var state: CuteCardPhase by mutableStateOf(CuteCardPhase.Front)
        private set

    /**
     * True while the card is in [CuteCardPhase.Settling].
     * The composable watches this and runs the settle timer [LaunchedEffect].
     */
    val shouldRunSettleTimer: Boolean
        get() = state is CuteCardPhase.Settling

    // Transition API

    /**
     * Called when the user taps the card.
     * Valid from: [CuteCardPhase.Front] → starts flip.
     * Valid from: [CuteCardPhase.Back] → starts confirm exit.
     * All other states: ignored (interaction is locked).
     */
    fun onCardTap() {
        when (state) {
            CuteCardPhase.Front -> transitionTo(CuteCardPhase.Flipping)
            CuteCardPhase.Back  -> transitionTo(CuteCardPhase.ExitingConfirm)
            else                 -> Unit
        }
    }

    /**
     * Called when the dismiss button is tapped.
     * Valid from: [CuteCardPhase.Back] only.
     * All other states: ignored.
     */
    fun onDismissTap() {
        if (state is CuteCardPhase.Back) {
            transitionTo(CuteCardPhase.ExitingDismiss)
        }
    }

    /**
     * Called when the unflip button is tapped.
     * Valid from: [CuteCardPhase.Back] only — starts the reverse flip animation.
     * All other states: ignored.
     */
    fun onUnflipTap() {
        if (state is CuteCardPhase.Back) {
            transitionTo(CuteCardPhase.UnFlipping)
        }
    }

    // Code-driven transitions
    //
    // Separate from the tap handlers above because a tap on the back face means
    // "I know it" and starts the confirm exit. Code asking for a flip never means that.

    /**
     * Flips the card from code, as a tap on the front face would.
     * Valid from: [CuteCardPhase.Front] only. All other states: ignored.
     */
    fun requestFlip() {
        if (state is CuteCardPhase.Front) {
            transitionTo(CuteCardPhase.Flipping)
        }
    }

    /**
     * True when [requestDismiss] would start an exit — the card is resting on either face.
     */
    val canRequestDismiss: Boolean
        get() = state is CuteCardPhase.Front || state is CuteCardPhase.Back

    /**
     * Dismisses the card from code, e.g. a consumer-owned "I don't know" button.
     * Unlike [onDismissTap], also valid from [CuteCardPhase.Front].
     * All other states: ignored.
     */
    fun requestDismiss() {
        when (state) {
            CuteCardPhase.Front -> transitionTo(CuteCardPhase.ExitingDismissFromFront)
            CuteCardPhase.Back  -> transitionTo(CuteCardPhase.ExitingDismiss)
            else                 -> Unit
        }
    }

    /**
     * Reverse-flips the card from code, as the unflip button would.
     * Valid from: [CuteCardPhase.Back] only. All other states: ignored.
     */
    fun requestUnflip() {
        if (state is CuteCardPhase.Back) {
            transitionTo(CuteCardPhase.UnFlipping)
        }
    }

    /**
     * Called by the composable when the reverse flip animation finishes.
     * Moves from [CuteCardPhase.UnFlipping] → [CuteCardPhase.Front].
     */
    fun onUnflipAnimationFinished() {
        if (state is CuteCardPhase.UnFlipping) {
            transitionTo(CuteCardPhase.Front)
        }
    }

    /**
     * Called by the composable when the flip animation finishes.
     * Moves from [CuteCardPhase.Flipping] → [CuteCardPhase.Settling].
     */
    fun onFlipAnimationFinished() {
        if (state is CuteCardPhase.Flipping) {
            transitionTo(CuteCardPhase.Settling)
        }
    }

    /**
     * Called by the composable's [LaunchedEffect] after
     * [CuteCardConfig.settledLockDurationMs] elapses.
     * Moves from [CuteCardPhase.Settling] → [CuteCardPhase.Back].
     */
    fun onSettled() {
        if (state is CuteCardPhase.Settling) {
            transitionTo(CuteCardPhase.Back)
        }
    }

    /**
     * Called by the composable when the exit animation finishes.
     * Moves from any exiting state → [CuteCardPhase.Gone].
     */
    fun onExitAnimationFinished() {
        when (state) {
            CuteCardPhase.ExitingConfirm,
            CuteCardPhase.ExitingDismiss,
            CuteCardPhase.ExitingDismissFromFront -> transitionTo(CuteCardPhase.Gone)
            else                          -> Unit
        }
    }

    /**
     * Resets the state machine to [CuteCardPhase.Front].
     * Called when [CuteCardContent] changes — i.e. a new card is shown.
     */
    fun reset() {
        transitionTo(CuteCardPhase.Front)
    }

    // Internal

    private fun transitionTo(next: CuteCardPhase) {
        state = next
    }
}
