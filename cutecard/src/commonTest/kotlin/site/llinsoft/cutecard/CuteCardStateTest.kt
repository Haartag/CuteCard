package site.llinsoft.cutecard

import site.llinsoft.cutecard.internal.state.CuteCardPhase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CuteCardStateTest {

    @Test
    fun canDismissFromFrontByDefault() {
        assertTrue(CuteCardState().canDismiss)
    }

    @Test
    fun dismissFromFrontStartsFrontExit() {
        val s = CuteCardState()
        s.dismiss()
        assertEquals(CuteCardPhase.ExitingDismissFromFront, s.holder.state)
    }

    @Test
    fun dismissFromBackStartsDismissExit() {
        val s = CuteCardState()
        s.holder.requestFlip(); s.holder.onFlipAnimationFinished(); s.holder.onSettled()
        s.dismiss()
        assertEquals(CuteCardPhase.ExitingDismiss, s.holder.state)
    }

    @Test
    fun actionsDisabledBlocksDismiss() {
        val s = CuteCardState()
        s.actionsEnabled = false
        assertFalse(s.canDismiss)
        s.dismiss()
        assertEquals(CuteCardPhase.Front, s.holder.state)
    }

    @Test
    fun canDismissFalseDuringFlip() {
        val s = CuteCardState()
        s.holder.requestFlip()
        assertFalse(s.canDismiss)
    }
}
