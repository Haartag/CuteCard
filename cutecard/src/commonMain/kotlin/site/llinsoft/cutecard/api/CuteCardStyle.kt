package site.llinsoft.cutecard

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * Controls the visual appearance of the audio button's two states.
 * Behavior and timing are controlled via [CuteCardConfig].
 *
 * @param idleIconColor Icon stroke color when audio is not playing.
 * @param playingIconColor Icon stroke color when audio is playing.
 * @param idleStrokeWidth Icon stroke width when audio is not playing.
 * @param playingStrokeWidth Icon stroke width when audio is playing. Should be
 * noticeably thicker than [idleStrokeWidth] for clear feedback.
 * @param idleContainerColor Background color of the button surface when audio is not playing.
 * @param playingContainerColor Background color of the button surface when audio is playing.
 * @param idleContentColor Color of the button label text when audio is not playing.
 * @param playingContentColor Color of the button label text when audio is playing.
 * @param shape Shape of the button surface.
 * @param textStyle Typography of the button label.
 */
@Immutable
data class AudioButtonStyle(
    val idleIconColor: Color,
    val playingIconColor: Color,
    val idleStrokeWidth: Dp,
    val playingStrokeWidth: Dp,
    val idleContainerColor: Color,
    val playingContainerColor: Color,
    val idleContentColor: Color,
    val playingContentColor: Color,
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
 * Visual appearance of the language indicator pill shown in the top-left corner of each card face.
 *
 * Front and back faces share [textColor] and [containerColor] by default.
 * Supply [backTextColor] or [backContainerColor] to override colors on the back face only.
 *
 * @param textStyle Typography of the pill label. Should be small, bold, and uppercase.
 * @param textColor Color of the pill label text on the front face.
 * @param containerColor Background fill of the pill on the front face.
 * @param shape Shape of the pill. Defaults to fully rounded in [CuteCardDefaults].
 * @param paddingHorizontal Horizontal padding inside the pill (between text and pill edge).
 * @param paddingVertical Vertical padding inside the pill (between text and pill edge).
 * @param cornerPadding Distance from the card corner to the pill edge.
 * @param backTextColor Text color override for the back face. Falls back to [textColor] when null.
 * @param backContainerColor Background color override for the back face. Falls back to [containerColor] when null.
 */
@Immutable
data class LanguagePillStyle(
    val textStyle: TextStyle,
    val textColor: Color,
    val containerColor: Color,
    val shape: Shape,
    val paddingHorizontal: Dp,
    val paddingVertical: Dp,
    val cornerPadding: Dp,
    val backTextColor: Color? = null,
    val backContainerColor: Color? = null
)

/**
 * Geometry and opacity of the decorative ghost cards behind the active card.
 * "Step" 1 is the card directly behind the active one, step 2 the one behind that.
 *
 * @param sideInsetPerStep How much narrower each lower card is on each side, per step.
 * Cards keep the full card height. `null` (default) scales each card down instead
 * (95 % and 90 % of the active card's size).
 * @param offsetPerStep How far each lower card is shifted down, per step.
 * `null` (default) uses 24 dp and 40 dp.
 * @param nearCardAlpha Opacity of the card directly behind the active one.
 * @param farCardAlpha Opacity of the card at the back of the stack.
 */
@Immutable
data class GhostStackStyle(
    val sideInsetPerStep: Dp? = null,
    val offsetPerStep: Dp? = null,
    val nearCardAlpha: Float = 0.85f,
    val farCardAlpha: Float = 0.65f
) {
    init {
        require(sideInsetPerStep == null || sideInsetPerStep >= 0.dp) { "sideInsetPerStep must be >= 0.dp" }
        require(offsetPerStep == null || offsetPerStep >= 0.dp) { "offsetPerStep must be >= 0.dp" }
        require(nearCardAlpha in 0f..1f) { "nearCardAlpha must be in 0..1" }
        require(farCardAlpha in 0f..1f) { "farCardAlpha must be in 0..1" }
    }
}

/**
 * Vertical position of the word inside the word area — the space left after
 * [CuteCardStyle.contentPadding] and the reserved top / bottom rows.
 *
 * [Raised]   Free space split 40 / 60 above / below the word and its details, so the
 *            block sits slightly above centre (default)
 * [Centered] The word itself is exactly centred; phonetics and word class hang below it
 *            without moving it, so the word sits at the same spot on both faces
 */
enum class WordVerticalPosition {
    Raised,
    Centered
}

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
 * @param wordTextStyle Typography of the primary word / translation. Used on both faces
 * unless [backWordTextStyle] is set.
 * @param wordTextColor Color of the primary word / translation text. Used on both faces
 * unless [backWordTextColor] is set.
 * @param phoneticsTextStyle Typography of the phonetic transcription.
 * @param phoneticsTextColor Color of the phonetic transcription text.
 * @param wordClassPillStyle Full appearance of the word class pill.
 * @param audioButtonStyle Full appearance of the audio button.
 * @param dismissButtonStyle Full appearance of the dismiss button.
 * @param languagePillStyle Full appearance of the language indicator pill in the card corner.
 * @param contentPadding Space between the card edge and its content, on all sides.
 * Also insets the audio button.
 * @param reservedTopHeight Height of a row at the top of the word area that the word never
 * enters. Size it to the corner icons (e.g. [UnflipButtonPosition.CardTopEnd]). Applies to
 * both faces, so the word stays in the same place whichever face has the icons.
 * @param reservedBottomHeight Height of a row at the bottom of the word area that the word
 * never enters. Size it to the audio button. Applies to both faces.
 * @param wordVerticalPosition Where the word sits in the word area. See [WordVerticalPosition].
 * @param backWordTextStyle Typography of the main text on the back face. Merged over
 * [wordTextStyle], so set only what differs, e.g. a font family. `null` = same as the front.
 * @param backWordTextColor Color of the main text on the back face. `null` = [wordTextColor].
 * @param ghostStackStyle Size, offset and opacity of the ghost cards. See [GhostStackStyle].
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
    /** Minimum font size the word / translation text will shrink to when auto-sizing. */
    val wordAutoSizeMinFontSize: TextUnit,
    val phoneticsTextStyle: TextStyle,
    val phoneticsTextColor: Color,
    val wordClassPillStyle: WordClassPillStyle,
    val audioButtonStyle: AudioButtonStyle,
    val dismissButtonStyle: DismissButtonStyle,
    val languagePillStyle: LanguagePillStyle,
    val contentPadding: Dp = 28.dp,
    val reservedTopHeight: Dp = 0.dp,
    val reservedBottomHeight: Dp = 0.dp,
    val wordVerticalPosition: WordVerticalPosition = WordVerticalPosition.Raised,
    val backWordTextStyle: TextStyle? = null,
    val backWordTextColor: Color? = null,
    val ghostStackStyle: GhostStackStyle = GhostStackStyle()
)
