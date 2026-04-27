package com.llinsoft.cutecard

import com.llinsoft.cutecard.api.CardFrontSide
import com.llinsoft.cutecard.api.ExitAnimation
import com.llinsoft.cutecard.api.CuteCardConfig
import com.llinsoft.cutecard.api.CuteCardContent
import com.llinsoft.cutecard.api.CuteCardLabels
import com.llinsoft.cutecard.api.FlipDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CuteCardApiTest {

    // ── CuteCardContent ──────────────────────────────────────────────────────

    @Test
    fun contentRequiredFieldsOnly() {
        val c = CuteCardContent(word = "hierro", translation = "iron")
        assertEquals("hierro", c.word)
        assertEquals("iron", c.translation)
        assertNull(c.phonetics)
        assertNull(c.wordClass)
        assertNull(c.audioUrl)
    }

    @Test
    fun contentAllFields() {
        val c = CuteCardContent(
            word = "hierro",
            translation = "iron",
            phonetics = "[ˈje.ro]",
            wordClass = "noun",
            audioUrl = "https://example.com/hierro.mp3"
        )
        assertEquals("[ˈje.ro]", c.phonetics)
        assertEquals("noun", c.wordClass)
        assertEquals("https://example.com/hierro.mp3", c.audioUrl)
    }

    @Test
    fun contentCopyChangesOnlySpecifiedField() {
        val original = CuteCardContent(word = "hierro", translation = "iron", phonetics = "[ˈje.ro]")
        val copy = original.copy(word = "agua")
        assertEquals("agua", copy.word)
        assertEquals("iron", copy.translation)
        assertEquals("[ˈje.ro]", copy.phonetics)
    }

    @Test
    fun contentEqualityByValue() {
        val a = CuteCardContent(word = "hierro", translation = "iron")
        val b = CuteCardContent(word = "hierro", translation = "iron")
        assertEquals(a, b)
    }

    // ── CuteCardConfig ───────────────────────────────────────────────────────

    @Test
    fun configDefaults() {
        val c = CuteCardConfig()
        assertEquals(CardFrontSide.Word, c.frontSide)
        assertEquals(400, c.flipDurationMs)
        assertEquals(350, c.settledLockDurationMs)
        assertEquals(300, c.exitDurationMs)
        assertEquals(FlipDirection.Horizontal, c.flipDirection)
        assertEquals(ExitAnimation.SlideUp, c.confirmExit)
        assertEquals(ExitAnimation.SlideDown, c.dismissExit)
    }

    @Test
    fun configTranslationMode() {
        val c = CuteCardConfig(frontSide = CardFrontSide.Translation)
        assertEquals(CardFrontSide.Translation, c.frontSide)
    }

    @Test
    fun configVerticalFlip() {
        val c = CuteCardConfig(flipDirection = FlipDirection.Vertical)
        assertEquals(FlipDirection.Vertical, c.flipDirection)
    }

    @Test
    fun configConfirmExitNone() {
        val c = CuteCardConfig(confirmExit = ExitAnimation.None)
        assertEquals(ExitAnimation.None, c.confirmExit)
        assertEquals(ExitAnimation.SlideDown, c.dismissExit)
    }

    @Test
    fun configDismissExitNone() {
        val c = CuteCardConfig(dismissExit = ExitAnimation.None)
        assertEquals(ExitAnimation.SlideUp, c.confirmExit)
        assertEquals(ExitAnimation.None, c.dismissExit)
    }

    @Test
    fun configCopyPreservesUnchangedFields() {
        val original = CuteCardConfig(flipDurationMs = 600)
        val copy = original.copy(exitDurationMs = 200)
        assertEquals(600, copy.flipDurationMs)
        assertEquals(200, copy.exitDurationMs)
    }

    // ── CuteCardLabels ───────────────────────────────────────────────────────

    @Test
    fun labelsDefaults() {
        val l = CuteCardLabels()
        assertEquals("I don't know", l.dismissButtonLabel)
        assertEquals("Play word", l.audioButtonIdleLabel)
        assertEquals("Playing...", l.audioButtonPlayingLabel)
    }

    @Test
    fun labelsCanBeOverridden() {
        val l = CuteCardLabels(dismissButtonLabel = "Still learning")
        assertEquals("Still learning", l.dismissButtonLabel)
        assertEquals("Play word", l.audioButtonIdleLabel)
    }
}
