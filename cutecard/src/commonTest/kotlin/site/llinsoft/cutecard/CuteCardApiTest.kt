package site.llinsoft.cutecard

import androidx.compose.ui.unit.dp
import site.llinsoft.cutecard.CardFrontSide
import site.llinsoft.cutecard.ExitAnimation
import site.llinsoft.cutecard.CuteCardConfig
import site.llinsoft.cutecard.CuteCardContent
import site.llinsoft.cutecard.CuteCardLabels
import site.llinsoft.cutecard.FlipDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
        assertEquals(UnflipButtonPosition.None, c.unflipButtonPosition)
    }

    @Test
    fun unflipButtonPositionValues() {
        assertEquals(
            listOf(UnflipButtonPosition.None, UnflipButtonPosition.AboveCard, UnflipButtonPosition.CardTopEnd),
            UnflipButtonPosition.entries
        )
    }

    @Test
    fun wordVerticalPositionValues() {
        assertEquals(
            listOf(WordVerticalPosition.Raised, WordVerticalPosition.Centered),
            WordVerticalPosition.entries
        )
    }

    @Test
    fun ghostStackStyleDefaultsKeepLegacyLook() {
        val g = GhostStackStyle()
        assertNull(g.sideInsetPerStep)
        assertNull(g.offsetPerStep)
        assertEquals(0.85f, g.nearCardAlpha)
        assertEquals(0.65f, g.farCardAlpha)
    }

    @Test
    fun ghostStackStyleRejectsInvalidValues() {
        assertFailsWith<IllegalArgumentException> { GhostStackStyle(sideInsetPerStep = (-1).dp) }
        assertFailsWith<IllegalArgumentException> { GhostStackStyle(offsetPerStep = (-1).dp) }
        assertFailsWith<IllegalArgumentException> { GhostStackStyle(nearCardAlpha = 1.5f) }
        assertFailsWith<IllegalArgumentException> { GhostStackStyle(farCardAlpha = -0.1f) }
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
