package site.llinsoft.cutecard

import site.llinsoft.cutecard.internal.state.CuteCardPhase
import site.llinsoft.cutecard.internal.state.CuteCardStateHolder
import site.llinsoft.cutecard.internal.state.isBackVisible
import site.llinsoft.cutecard.internal.state.isInteractionLocked
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CuteCardStateMachineTest {

    private fun holder() = CuteCardStateHolder()

    // ── Initial state ────────────────────────────────────────────────────────

    @Test
    fun initialStateIsFront() {
        assertEquals(CuteCardPhase.Front, holder().state)
    }

    // ── Front → Flipping ─────────────────────────────────────────────────────

    @Test
    fun tapCardFromFrontStartsFlip() {
        val h = holder()
        h.onCardTap()
        assertEquals(CuteCardPhase.Flipping, h.state)
    }

    // ── Flipping → Settling ───────────────────────────────────────────────────

    @Test
    fun flipAnimationFinishedTransitionsToSettling() {
        val h = holder()
        h.onCardTap()
        h.onFlipAnimationFinished()
        assertEquals(CuteCardPhase.Settling, h.state)
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
        assertEquals(CuteCardPhase.Back, h.state)
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
        assertEquals(CuteCardPhase.ExitingConfirm, h.state)
    }

    // ── Back → UnFlipping ─────────────────────────────────────────────────────

    @Test
    fun unflipTapFromBackStartsUnFlipping() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished(); h.onSettled()
        h.onUnflipTap()
        assertEquals(CuteCardPhase.UnFlipping, h.state)
    }

    // ── UnFlipping → Front ────────────────────────────────────────────────────

    @Test
    fun unflipAnimationFinishedTransitionsToFront() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished(); h.onSettled()
        h.onUnflipTap()
        h.onUnflipAnimationFinished()
        assertEquals(CuteCardPhase.Front, h.state)
    }

    @Test
    fun unflipTapFromFrontIsIgnored() {
        val h = holder()
        h.onUnflipTap()
        assertEquals(CuteCardPhase.Front, h.state)
    }

    @Test
    fun unflipTapFromFlippingIsIgnored() {
        val h = holder()
        h.onCardTap()
        h.onUnflipTap()
        assertEquals(CuteCardPhase.Flipping, h.state)
    }

    @Test
    fun unflipAnimationFinishedOutsideUnFlippingIsIgnored() {
        val h = holder()
        h.onUnflipAnimationFinished()
        assertEquals(CuteCardPhase.Front, h.state)
    }

    // ── Back → ExitingDismiss ─────────────────────────────────────────────────

    @Test
    fun dismissTapFromBackStartsDismissExit() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished(); h.onSettled()
        h.onDismissTap()
        assertEquals(CuteCardPhase.ExitingDismiss, h.state)
    }

    // ── ExitingConfirm → Gone ─────────────────────────────────────────────────

    @Test
    fun exitAnimationFinishedFromConfirmTransitionsToGone() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished(); h.onSettled()
        h.onCardTap()
        h.onExitAnimationFinished()
        assertEquals(CuteCardPhase.Gone, h.state)
    }

    // ── ExitingDismiss → Gone ─────────────────────────────────────────────────

    @Test
    fun exitAnimationFinishedFromDismissTransitionsToGone() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished(); h.onSettled()
        h.onDismissTap()
        h.onExitAnimationFinished()
        assertEquals(CuteCardPhase.Gone, h.state)
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
            assertEquals(CuteCardPhase.Front, h.state, "reset() should return to Front")
        }
    }

    // ── Interaction lock guards ───────────────────────────────────────────────

    @Test
    fun tapDuringFlippingIsIgnored() {
        val h = holder()
        h.onCardTap()                         // → Flipping
        h.onCardTap()                         // should be ignored
        assertEquals(CuteCardPhase.Flipping, h.state)
    }

    @Test
    fun tapDuringSettlingIsIgnored() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished()   // → Settling
        h.onCardTap()
        assertEquals(CuteCardPhase.Settling, h.state)
    }

    @Test
    fun dismissDuringFrontIsIgnored() {
        val h = holder()
        h.onDismissTap()
        assertEquals(CuteCardPhase.Front, h.state)
    }

    @Test
    fun dismissDuringFlippingIsIgnored() {
        val h = holder()
        h.onCardTap()
        h.onDismissTap()
        assertEquals(CuteCardPhase.Flipping, h.state)
    }

    @Test
    fun flipAnimationFinishedOutsideFlippingIsIgnored() {
        val h = holder()
        h.onFlipAnimationFinished()
        assertEquals(CuteCardPhase.Front, h.state)
    }

    @Test
    fun onSettledOutsideSettlingIsIgnored() {
        val h = holder()
        h.onSettled()
        assertEquals(CuteCardPhase.Front, h.state)
    }

    @Test
    fun exitAnimationFinishedOutsideExitingIsIgnored() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished(); h.onSettled()  // → Back
        h.onExitAnimationFinished()
        assertEquals(CuteCardPhase.Back, h.state)
    }

    // ── isInteractionLocked extension ─────────────────────────────────────────

    @Test
    fun frontIsNotLocked() {
        assertFalse(CuteCardPhase.Front.isInteractionLocked)
    }

    @Test
    fun backIsNotLocked() {
        assertFalse(CuteCardPhase.Back.isInteractionLocked)
    }

    @Test
    fun flippingIsLocked() {
        assertTrue(CuteCardPhase.Flipping.isInteractionLocked)
    }

    @Test
    fun unFlippingIsLocked() {
        assertTrue(CuteCardPhase.UnFlipping.isInteractionLocked)
    }

    @Test
    fun settlingIsLocked() {
        assertTrue(CuteCardPhase.Settling.isInteractionLocked)
    }

    @Test
    fun exitingConfirmIsLocked() {
        assertTrue(CuteCardPhase.ExitingConfirm.isInteractionLocked)
    }

    @Test
    fun exitingDismissIsLocked() {
        assertTrue(CuteCardPhase.ExitingDismiss.isInteractionLocked)
    }

    @Test
    fun goneIsLocked() {
        assertTrue(CuteCardPhase.Gone.isInteractionLocked)
    }

    // ── isBackVisible extension ───────────────────────────────────────────────

    @Test
    fun frontIsNotBackVisible() {
        assertFalse(CuteCardPhase.Front.isBackVisible)
    }

    @Test
    fun flippingIsNotBackVisible() {
        assertFalse(CuteCardPhase.Flipping.isBackVisible)
    }

    @Test
    fun unFlippingIsNotBackVisible() {
        assertFalse(CuteCardPhase.UnFlipping.isBackVisible)
    }

    @Test
    fun settlingIsBackVisible() {
        assertTrue(CuteCardPhase.Settling.isBackVisible)
    }

    @Test
    fun backIsBackVisible() {
        assertTrue(CuteCardPhase.Back.isBackVisible)
    }

    @Test
    fun exitingConfirmIsBackVisible() {
        assertTrue(CuteCardPhase.ExitingConfirm.isBackVisible)
    }

    @Test
    fun exitingDismissIsBackVisible() {
        assertTrue(CuteCardPhase.ExitingDismiss.isBackVisible)
    }

    @Test
    fun goneIsNotBackVisible() {
        assertFalse(CuteCardPhase.Gone.isBackVisible)
    }

    // ── Code-driven flip ──────────────────────────────────────────────────────

    @Test
    fun requestFlipFromFrontStartsFlip() {
        val h = holder()
        h.requestFlip()
        assertEquals(CuteCardPhase.Flipping, h.state)
    }

    @Test
    fun requestFlipFromFlippingIsIgnored() {
        val h = holder()
        h.requestFlip()
        h.requestFlip()
        assertEquals(CuteCardPhase.Flipping, h.state)
    }

    @Test
    fun requestFlipFromSettlingIsIgnored() {
        val h = holder()
        h.requestFlip(); h.onFlipAnimationFinished()
        h.requestFlip()
        assertEquals(CuteCardPhase.Settling, h.state)
    }

    @Test
    fun requestFlipFromBackIsIgnored() {
        val h = holder()
        h.requestFlip(); h.onFlipAnimationFinished(); h.onSettled()
        h.requestFlip()
        assertEquals(CuteCardPhase.Back, h.state)
    }

    @Test
    fun requestFlipFromGoneIsIgnored() {
        val h = holder()
        h.requestFlip(); h.onFlipAnimationFinished(); h.onSettled()
        h.onCardTap(); h.onExitAnimationFinished()
        h.requestFlip()
        assertEquals(CuteCardPhase.Gone, h.state)
    }

    @Test
    fun requestUnflipFromBackStartsUnFlipping() {
        val h = holder()
        h.requestFlip(); h.onFlipAnimationFinished(); h.onSettled()
        h.requestUnflip()
        assertEquals(CuteCardPhase.UnFlipping, h.state)
    }

    @Test
    fun requestUnflipFromFrontIsIgnored() {
        val h = holder()
        h.requestUnflip()
        assertEquals(CuteCardPhase.Front, h.state)
    }

    @Test
    fun requestUnflipFromSettlingIsIgnored() {
        val h = holder()
        h.requestFlip(); h.onFlipAnimationFinished()
        h.requestUnflip()
        assertEquals(CuteCardPhase.Settling, h.state)
    }

    @Test
    fun requestUnflipFromUnFlippingIsIgnored() {
        val h = holder()
        h.requestFlip(); h.onFlipAnimationFinished(); h.onSettled()
        h.requestUnflip()
        h.requestUnflip()
        assertEquals(CuteCardPhase.UnFlipping, h.state)
    }

    @Test
    fun requestFlipWorksAfterReset() {
        val h = holder()
        h.requestFlip(); h.onFlipAnimationFinished(); h.onSettled()
        h.reset()
        h.requestFlip()
        assertEquals(CuteCardPhase.Flipping, h.state)
    }

    @Test
    fun codeDrivenRoundTripReturnsToFront() {
        val h = holder()
        h.requestFlip()
        assertEquals(CuteCardPhase.Flipping, h.state)
        h.onFlipAnimationFinished()
        h.onSettled()
        assertEquals(CuteCardPhase.Back, h.state)
        h.requestUnflip()
        assertEquals(CuteCardPhase.UnFlipping, h.state)
        h.onUnflipAnimationFinished()
        assertEquals(CuteCardPhase.Front, h.state)
    }

    @Test
    fun requestFlipDoesNotConfirmFromBack() {
        val h = holder()
        h.requestFlip(); h.onFlipAnimationFinished(); h.onSettled()
        h.requestFlip()
        assertFalse(h.state is CuteCardPhase.ExitingConfirm)
    }

    // ── Code-driven dismiss ───────────────────────────────────────────────────

    @Test
    fun requestDismissFromFrontExitsWithoutFlipping() {
        val h = holder()
        h.requestDismiss()
        assertEquals(CuteCardPhase.ExitingDismissFromFront, h.state)
        assertFalse(h.state.isBackVisible)
    }

    @Test
    fun requestDismissFromBackStartsDismissExit() {
        val h = holder()
        h.onCardTap(); h.onFlipAnimationFinished(); h.onSettled()
        h.requestDismiss()
        assertEquals(CuteCardPhase.ExitingDismiss, h.state)
    }

    @Test
    fun requestDismissDuringAnimationsIsIgnored() {
        fun setup(block: CuteCardStateHolder.() -> Unit) = block
        val setups = listOf(
            setup { onCardTap() } to CuteCardPhase.Flipping,
            setup { onCardTap(); onFlipAnimationFinished() } to CuteCardPhase.Settling,
            setup { onCardTap(); onFlipAnimationFinished(); onSettled(); onUnflipTap() } to CuteCardPhase.UnFlipping,
            setup { requestDismiss() } to CuteCardPhase.ExitingDismissFromFront,
        )
        for ((prepare, expected) in setups) {
            val h = holder()
            h.prepare()
            h.requestDismiss()
            assertEquals(expected, h.state)
        }
    }

    @Test
    fun exitAnimationFinishedFromFrontDismissTransitionsToGone() {
        val h = holder()
        h.requestDismiss()
        h.onExitAnimationFinished()
        assertEquals(CuteCardPhase.Gone, h.state)
    }

    @Test
    fun canRequestDismissOnlyOnRestingFaces() {
        val h = holder()
        assertTrue(h.canRequestDismiss)
        h.onCardTap()
        assertFalse(h.canRequestDismiss)
        h.onFlipAnimationFinished()
        assertFalse(h.canRequestDismiss)
        h.onSettled()
        assertTrue(h.canRequestDismiss)
        h.requestDismiss()
        assertFalse(h.canRequestDismiss)
    }

    @Test
    fun dismissTapFromFrontStillIgnored() {
        val h = holder()
        h.onDismissTap()
        assertEquals(CuteCardPhase.Front, h.state)
    }

    @Test
    fun exitingDismissFromFrontIsLocked() {
        assertTrue(CuteCardPhase.ExitingDismissFromFront.isInteractionLocked)
    }
}
