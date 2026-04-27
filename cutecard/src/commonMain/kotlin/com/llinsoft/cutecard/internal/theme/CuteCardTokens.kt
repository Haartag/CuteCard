package com.llinsoft.cutecard.internal.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Internal design tokens for the CuteCard component.
 * These are never exposed to the consumer — all customisation
 * goes through [CuteCardStyle] and [CuteCardConfig].
 *
 * Changing a token here propagates automatically to every
 * internal composable that references it.
 */
internal object CuteCardTokens {

    // Card

    /** Horizontal padding from screen edge to card edge. */
    val CardHorizontalPadding = 24.dp

    /** Corner radius of the card surface. */
    val CardCornerRadius = 28.dp

    /** Default aspect ratio of the card (width:height = 3:4). */
    const val CardAspectRatio = 3f / 4f

    /** Elevation of the active (top) card. */
    val CardElevation = 6.dp

    /** Internal padding inside the card surface. */
    val CardContentPadding = 28.dp

    // Ghost stack

    /** Scale factor of the second card in the ghost stack. */
    const val GhostCard2Scale = 0.95f

    /** Scale factor of the third card in the ghost stack. */
    const val GhostCard3Scale = 0.90f

    /** Vertical downward offset of the second ghost card. */
    val GhostCard2OffsetY = 24.dp

    /** Vertical downward offset of the third ghost card. */
    val GhostCard3OffsetY = 40.dp

    /** Elevation of the second ghost card. */
    val GhostCard2Elevation = 3.dp

    /** Elevation of the third ghost card. */
    val GhostCard3Elevation = 1.5.dp

    /** Opacity of the second ghost card surface. */
    const val GhostCard2Alpha = 0.85f

    /** Opacity of the third ghost card surface. */
    const val GhostCard3Alpha = 0.65f

    // Typography

    /** Font size of the primary word / translation. */
    val WordTextSize = 40.sp

    /** Font size of the phonetic transcription. */
    val PhoneticsTextSize = 14.sp

    /** Font size of the word class pill label. */
    val WordClassTextSize = 11.sp

    // Word class pill

    /** Horizontal padding inside the word class pill. */
    val WordClassPillPaddingHorizontal = 11.dp

    /** Vertical padding inside the word class pill. */
    val WordClassPillPaddingVertical = 3.dp

    /** Corner radius of the word class pill. */
    val WordClassPillCornerRadius = 100.dp

    // Audio button

    /** Stroke width of the audio icon in idle state. */
    val AudioIconIdleStrokeWidth = 2.dp

    /** Stroke width of the audio icon in playing state. */
    val AudioIconPlayingStrokeWidth = 2.8.dp

    /** Size of the audio icon itself. */
    val AudioIconSize = 20.dp

    /** Vertical padding inside the audio button. */
    val AudioButtonPaddingVertical = 14.dp

    /** Corner radius of the audio button surface. */
    val AudioButtonCornerRadius = 14.dp

    /** Duration of the idle ↔ playing color and stroke transition. */
    const val AudioButtonTransitionDurationMs = 200

    // Dismiss button

    /** Vertical gap between the bottom of the card and the dismiss button. */
    val DismissButtonTopPadding = 48.dp

    /** Horizontal padding inside the dismiss button tap target. */
    val DismissButtonPaddingHorizontal = 24.dp

    /** Vertical padding inside the dismiss button tap target. */
    val DismissButtonPaddingVertical = 10.dp

    /** Space reserved in the layout for the dismiss button even when it is hidden. */
    val DismissButtonReservedHeight = 48.dp

    // Spacing

    /** Gap between word and phonetics. */
    val WordToPhoneticsSpacing = 10.dp

    /** Gap between phonetics and word class pill. */
    val PhoneticsToWordClassSpacing = 14.dp

    /** Gap between the main text block and the audio button on the back face. */
    val ContentToAudioButtonSpacing = 16.dp

}
