package site.llinsoft.cutecard

import androidx.compose.runtime.Immutable

/**
 * All user-facing strings for [CuteCard].
 * Replace any value to localise or rebrand.
 *
 * @param dismissButtonLabel Label on the "I don't know" button.
 * @param unflipButtonLabel Label on the button that flips the card back to the front.
 * @param audioButtonIdleLabel Label on the audio button in its idle state.
 * @param audioButtonPlayingLabel Label on the audio button while audio is playing.
 * @param audioButtonContentDescription Accessibility description for the audio button.
 * Used by screen readers.
 * @param cardFrontContentDescription Accessibility description for the card in its front state.
 * Used by screen readers.
 * @param cardBackContentDescription Accessibility description for the card in its back state.
 * Used by screen readers.
 */
@Immutable
data class CuteCardLabels(
    val dismissButtonLabel: String = "I don't know",
    val unflipButtonLabel: String = "Show word",
    val audioButtonIdleLabel: String = "Play word",
    val audioButtonPlayingLabel: String = "Playing...",
    val audioButtonContentDescription: String = "Play pronunciation",
    val cardFrontContentDescription: String = "Word card, tap to reveal translation",
    val cardBackContentDescription: String = "Translation revealed, tap to mark as known"
)
