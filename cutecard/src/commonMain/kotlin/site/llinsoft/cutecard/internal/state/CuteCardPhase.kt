package site.llinsoft.cutecard.internal.state

/**
 * Lifecycle state machine for [CuteCard].
 * Flow: [Front] → [Flipping] → [Settling] → [Back] → [ExitingConfirm] or [ExitingDismiss] → [Gone].
 * A code-driven dismiss can also leave from the front: [Front] → [ExitingDismissFromFront] → [Gone].
 * [Settling] is a brief tap-lock after the flip to prevent accidental double-tap confirmation.
 */
internal sealed class CuteCardPhase {

    /**
     * Initial state. Main face of card is visible.
     * Card is fully interactive — tap triggers the flip.
     */
    data object Front : CuteCardPhase()

    /**
     * 3D flip animation is running. Card is not interactive.
     * Transitions automatically to [Settling] when animation completes.
     */
    data object Flipping : CuteCardPhase()

    /**
     * Reverse 3D flip animation is running (back → front). Card is not interactive.
     * Transitions automatically to [Front] when animation completes.
     */
    data object UnFlipping : CuteCardPhase()

    /**
     * Flip animation has completed. The Back face is visible but the card
     * is temporarily non-interactive for [CuteCardConfig.settledLockDurationMs].
     * Prevents accidental double-tap from immediately confirming the card.
     * Transitions automatically to [Back] when the lock duration elapses.
     */
    data object Settling : CuteCardPhase()

    /**
     * The Back face is fully visible and interactive.
     * Tap card → transitions to [ExitingConfirm].
     * Tap dismiss button → transitions to [ExitingDismiss].
     */
    data object Back : CuteCardPhase()

    /**
     * Success path. The confirm exit animation is running.
     * Card is not interactive. Transitions to [Gone] when animation completes.
     */
    data object ExitingConfirm : CuteCardPhase()

    /**
     * Fail path. The dismiss exit animation is running.
     * Card is not interactive. Transitions to [Gone] when animation completes.
     */
    data object ExitingDismiss : CuteCardPhase()

    /**
     * Fail path started from the front face via [CuteCardState.dismiss].
     * Separate from [ExitingDismiss] so the card exits without flipping to its back.
     * Card is not interactive. Transitions to [Gone] when animation completes.
     */
    data object ExitingDismissFromFront : CuteCardPhase()

    /**
     * Terminal state. Exit animation has completed.
     * [CuteCard] calls [onKnown] or [onUnknown] and the consumer
     * takes over — typically replacing this card with the next one.
     */
    data object Gone : CuteCardPhase()
}

/**
 * Returns true when the card is in a state where taps should be ignored.
 */
internal val CuteCardPhase.isInteractionLocked: Boolean
    get() = this is CuteCardPhase.Flipping
        || this is CuteCardPhase.UnFlipping
        || this is CuteCardPhase.Settling
        || this is CuteCardPhase.ExitingConfirm
        || this is CuteCardPhase.ExitingDismiss
        || this is CuteCardPhase.ExitingDismissFromFront
        || this is CuteCardPhase.Gone

/**
 * Returns true when the card is showing its back face
 * (i.e. the flip has completed and the card is settled or exiting).
 */
internal val CuteCardPhase.isBackVisible: Boolean
    get() = this is CuteCardPhase.Settling
        || this is CuteCardPhase.Back
        || this is CuteCardPhase.ExitingConfirm
        || this is CuteCardPhase.ExitingDismiss
