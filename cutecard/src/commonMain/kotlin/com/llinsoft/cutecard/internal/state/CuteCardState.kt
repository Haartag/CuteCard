package com.llinsoft.cutecard.internal.state

/**
 * Lifecycle state machine for [CuteCard].
 * Flow: [Front] → [Flipping] → [Settling] → [Back] → [ExitingConfirm] or [ExitingDismiss] → [Gone].
 * [Settling] is a brief tap-lock after the flip to prevent accidental double-tap confirmation.
 */
internal sealed class CuteCardState {

    /**
     * Initial state. Main face of card is visible.
     * Card is fully interactive — tap triggers the flip.
     */
    data object Front : CuteCardState()

    /**
     * 3D flip animation is running. Card is not interactive.
     * Transitions automatically to [Settling] when animation completes.
     */
    data object Flipping : CuteCardState()

    /**
     * Flip animation has completed. The Back face is visible but the card
     * is temporarily non-interactive for [CuteCardConfig.settledLockDurationMs].
     * Prevents accidental double-tap from immediately confirming the card.
     * Transitions automatically to [Back] when the lock duration elapses.
     */
    data object Settling : CuteCardState()

    /**
     * The Back face is fully visible and interactive.
     * Tap card → transitions to [ExitingConfirm].
     * Tap dismiss button → transitions to [ExitingDismiss].
     */
    data object Back : CuteCardState()

    /**
     * Success path. The confirm exit animation is running.
     * Card is not interactive. Transitions to [Gone] when animation completes.
     */
    data object ExitingConfirm : CuteCardState()

    /**
     * Fail path. The dismiss exit animation is running.
     * Card is not interactive. Transitions to [Gone] when animation completes.
     */
    data object ExitingDismiss : CuteCardState()

    /**
     * Terminal state. Exit animation has completed.
     * [CuteCard] calls [onKnown] or [onUnknown] and the consumer
     * takes over — typically replacing this card with the next one.
     */
    data object Gone : CuteCardState()
}

/**
 * Returns true when the card is in a state where taps should be ignored.
 */
internal val CuteCardState.isInteractionLocked: Boolean
    get() = this is CuteCardState.Flipping
        || this is CuteCardState.Settling
        || this is CuteCardState.ExitingConfirm
        || this is CuteCardState.ExitingDismiss
        || this is CuteCardState.Gone

/**
 * Returns true when the card is showing its back face
 * (i.e. the flip has completed and the card is settled or exiting).
 */
internal val CuteCardState.isBackVisible: Boolean
    get() = this is CuteCardState.Settling
        || this is CuteCardState.Back
        || this is CuteCardState.ExitingConfirm
        || this is CuteCardState.ExitingDismiss
