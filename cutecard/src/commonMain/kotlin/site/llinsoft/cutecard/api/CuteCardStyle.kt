package site.llinsoft.cutecard.api

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

/**
 * Controls the visual appearance of the audio button's two states.
 * Behavior and timing are controlled via [CuteCardConfig].
 *
 * @param idleIconColor Icon stroke color when audio is not playing.
 * @param playingIconColor Icon stroke color when audio is playing.
 * @param idleStrokeWidth Icon stroke width when audio is not playing.
 * @param playingStrokeWidth Icon stroke width when audio is playing. Should be
 * noticeably thicker than [idleStrokeWidth] for clear feedback.
 * @param containerColor Background color of the button surface.
 * @param contentColor Color of the button label text.
 * @param shape Shape of the button surface.
 * @param textStyle Typography of the button label.
 */
@Immutable
data class AudioButtonStyle(
    val idleIconColor: Color,
    val playingIconColor: Color,
    val idleStrokeWidth: Dp,
    val playingStrokeWidth: Dp,
    val containerColor: Color,
    val contentColor: Color,
    val shape: Shape,
    val textStyle: TextStyle
)

/**
 * Controls the visual appearance of the dismiss button.
 *
 * @param contentColor Color of the label text.
 * @param textStyle Typography of the label.
 * @param shape Shape of the button tap target.
 * @param containerColor Background color. Transparent by default.
 */
@Immutable
data class DismissButtonStyle(
    val contentColor: Color,
    val textStyle: TextStyle,
    val shape: Shape,
    val containerColor: Color = Color.Transparent
)

/**
 * Visual appearance of the word class pill.
 *
 * @param textStyle Typography of the pill label.
 * @param textColor Color of the pill label text.
 * @param containerColor Background fill of the pill.
 * @param borderColor Stroke color of the pill border.
 * @param shape Shape of the pill. Defaults to fully rounded in [CuteCardDefaults].
 */
@Immutable
data class WordClassPillStyle(
    val textStyle: TextStyle,
    val textColor: Color,
    val containerColor: Color,
    val borderColor: Color,
    val shape: Shape
)

/**
 * Full visual appearance of [CuteCard].
 * All animation and behavior is controlled separately via [CuteCardConfig].
 *
 * Build via [CuteCardDefaults.style] to get out-of-box values,
 * then use [copy] to override what you need.
 *
 * @param cardShape Shape of the card surface.
 * @param cardElevation Shadow elevation of the active card.
 * @param cardAspectRatio Width-to-height ratio of the card. Default 3:4.
 * @param cardBackgroundColor Background fill of the card surface.
 * @param ghostCardBackgroundColor Background fill of the decorative stack cards behind the active card.
 * @param cardBorderColor Optional border stroke. Transparent = no border.
 * @param wordTextStyle Typography of the primary word / translation.
 * @param wordTextColor Color of the primary word / translation text.
 * @param phoneticsTextStyle Typography of the phonetic transcription.
 * @param phoneticsTextColor Color of the phonetic transcription text.
 * @param wordClassPillStyle Full appearance of the word class pill.
 * @param audioButtonStyle Full appearance of the audio button.
 * @param dismissButtonStyle Full appearance of the dismiss button.
 */
@Immutable
data class CuteCardStyle(
    val cardShape: Shape,
    val cardElevation: Dp,
    val cardAspectRatio: Float,
    val cardBackgroundColor: Color,
    val ghostCardBackgroundColor: Color,
    val cardBorderColor: Color,
    val wordTextStyle: TextStyle,
    val wordTextColor: Color,
    val phoneticsTextStyle: TextStyle,
    val phoneticsTextColor: Color,
    val wordClassPillStyle: WordClassPillStyle,
    val audioButtonStyle: AudioButtonStyle,
    val dismissButtonStyle: DismissButtonStyle
)
