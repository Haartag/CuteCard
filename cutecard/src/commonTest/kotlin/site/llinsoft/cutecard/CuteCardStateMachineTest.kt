package site.llinsoft.cutecard

import site.llinsoft.cutecard.api.CuteCardConfig
import site.llinsoft.cutecard.internal.state.CuteCardState
import site.llinsoft.cutecard.internal.state.CuteCardStateHolder
import site.llinsoft.cutecard.internal.state.isBackVisible
import site.llinsoft.cutecard.internal.state.isInteractionLocked
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CuteCardStateMachineTest {

    private fun holder() = CuteCardStateHolder(CuteCardConfig())

    // ── Initial state ────────────────────────────────────────────────────────

    @Test
    fun initialStateIsFront() {
        assertEquals(CuteCardState.Front, holder().state)
    }

    // ── Front → Flipping ─────────────────────────────────────────────────────

    @Test
    fun tapCardFromFrontStartsFlip() {
        val h = holder()
        h.onCardTap()
        assertEquals(CuteCardState.Flipping, h.state)
    }

    // ── Flipping → Settling ───────────────────────────────────────────────────

    @Test
    fun flipAnimationFinishedTransitionsToSettling() {
        val h = holder()
        h.onCardTap()
        h.onFlipAnimationFinished()
        assertEquals(CuteCardState.Settling, h.state)
    }

    @Test
    fun shouldRunSettleTimerIsTrueDuringSettling() {
        val h = holder()
        h.onCardTap()
        h.onFlipAnimationFinished()
        assertTrue(h.shouldRunSettleTimer)
    }

    // ── Settling → Back ───────────────────────────────────────────────────────

    @Test
    fun onSettledTransitionsToBack() {
        val h = holder()
        h.onCardTap()
        h.onFlipAnimationFinished()
        h.onSettled()
        assertEquals(CuteCardState.Back, h.state)
    }

    @Test
    fun shouldRunSettleTimerIsFalseAfterSettled() {
        val h = holder()
        h.onCardTap()
        h.onFlipAnimationFinished()
        h.onSettled()
        assertFalse(h.shouldRunSettleTimer)
    }

    // ── Back → ExitingConfirm ─────────────────────────────────────────────────

    @Test
    fun tapCardFromBackStartsConfirmExit() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished(); h.onSettled()
        h.onCardTap()
        assertEquals(CuteCardState.ExitingConfirm, h.state)
    }

    // ── Back → ExitingDismiss ─────────────────────────────────────────────────

    @Test
    fun dismissTapFromBackStartsDismissExit() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished(); h.onSettled()
        h.onDismissTap()
        assertEquals(CuteCardState.ExitingDismiss, h.state)
    }

    // ── ExitingConfirm → Gone ─────────────────────────────────────────────────

    @Test
    fun exitAnimationFinishedFromConfirmTransitionsToGone() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished(); h.onSettled()
        h.onCardTap()
        h.onExitAnimationFinished()
        assertEquals(CuteCardState.Gone, h.state)
    }

    // ── ExitingDismiss → Gone ─────────────────────────────────────────────────

    @Test
    fun exitAnimationFinishedFromDismissTransitionsToGone() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished(); h.onSettled()
        h.onDismissTap()
        h.onExitAnimationFinished()
        assertEquals(CuteCardState.Gone, h.state)
    }

    // ── Reset ─────────────────────────────────────────────────────────────────

    @Test
    fun resetFromAnyStateReturnsFront() {
        val states = listOf<CuteCardStateHolder.() -> Unit>(
            { },
            { onCardTap() },
            { onCardTap(); onFlipAnimationFinished() },
            { onCardTap(); onFlipAnimationFinished(); onSettled() },
        )
        for (setup in states) {
            val h = holder()
            h.setup()
            h.reset()
            assertEquals(CuteCardState.Front, h.state, "reset() should return to Front")
        }
    }

    // ── Interaction lock guards ───────────────────────────────────────────────

    @Test
    fun tapDuringFlippingIsIgnored() {
        val h = holder()
        h.onCardTap()                         // → Flipping
        h.onCardTap()                         // should be ignored
        assertEquals(CuteCardState.Flipping, h.state)
    }

    @Test
    fun tapDuringSettlingIsIgnored() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished()   // → Settling
        h.onCardTap()
        assertEquals(CuteCardState.Settling, h.state)
    }

    @Test
    fun dismissDuringFrontIsIgnored() {
        val h = holder()
        h.onDismissTap()
        assertEquals(CuteCardState.Front, h.state)
    }

    @Test
    fun dismissDuringFlippingIsIgnored() {
        val h = holder()
        h.onCardTap()
        h.onDismissTap()
        assertEquals(CuteCardState.Flipping, h.state)
    }

    @Test
    fun flipAnimationFinishedOutsideFlippingIsIgnored() {
        val h = holder()
        h.onFlipAnimationFinished()
        assertEquals(CuteCardState.Front, h.state)
    }

    @Test
    fun onSettledOutsideSettlingIsIgnored() {
        val h = holder()
        h.onSettled()
        assertEquals(CuteCardState.Front, h.state)
    }

    @Test
    fun exitAnimationFinishedOutsideExitingIsIgnored() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished(); h.onSettled()  // → Back
        h.onExitAnimationFinished()
        assertEquals(CuteCardState.Back, h.state)
    }

    // ── isInteractionLocked extension ─────────────────────────────────────────

    @Test
    fun frontIsNotLocked() {
        assertFalse(CuteCardState.Front.isInteractionLocked)
    }

    @Test
    fun backIsNotLocked() {
        assertFalse(CuteCardState.Back.isInteractionLocked)
    }

    @Test
    fun flippingIsLocked() {
        assertTrue(CuteCardState.Flipping.isInteractionLocked)
    }

    @Test
    fun settlingIsLocked() {
        assertTrue(CuteCardState.Settling.isInteractionLocked)
    }

    @Test
    fun exitingConfirmIsLocked() {
        assertTrue(CuteCardState.ExitingConfirm.isInteractionLocked)
    }

    @Test
    fun exitingDismissIsLocked() {
        assertTrue(CuteCardState.ExitingDismiss.isInteractionLocked)
    }

    @Test
    fun goneIsLocked() {
        assertTrue(CuteCardState.Gone.isInteractionLocked)
    }

    // ── isBackVisible extension ───────────────────────────────────────────────

    @Test
    fun frontIsNotBackVisible() {
        assertFalse(CuteCardState.Front.isBackVisible)
    }

    @Test
    fun flippingIsNotBackVisible() {
        assertFalse(CuteCardState.Flipping.isBackVisible)
    }

    @Test
    fun settlingIsBackVisible() {
        assertTrue(CuteCardState.Settling.isBackVisible)
    }

    @Test
    fun backIsBackVisible() {
        assertTrue(CuteCardState.Back.isBackVisible)
    }

    @Test
    fun exitingConfirmIsBackVisible() {
        assertTrue(CuteCardState.ExitingConfirm.isBackVisible)
    }

    @Test
    fun exitingDismissIsBackVisible() {
        assertTrue(CuteCardState.ExitingDismiss.isBackVisible)
    }

    @Test
    fun goneIsNotBackVisible() {
        assertFalse(CuteCardState.Gone.isBackVisible)
    }
}
