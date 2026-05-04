package site.llinsoft.cutecard

import androidx.compose.runtime.Immutable

/**
 * Controls which face of the card is shown first.
 * Enables two distinct exercise types with one component and one content object.
 *
 * [Word]        Front = word only → Back = full info + audio  (default)
 * [Translation] Front = full info + audio → Back = word only
 *
 * The audio button always lives on the "full info" side regardless of mode.
 */
enum class CardFrontSide {
    Word,
    Translation
}

/**
 * Controls the axis of the flip animation.
 *
 * [Horizontal] rotateY - card flips left to right (default)
 * [Vertical]   rotateX - card flips top to bottom
 */
enum class FlipDirection {
    Horizontal,
    Vertical
}

/**
 * Exit animation played when the consumer taps "I know it" (confirm)
 * or "I don't know" (dismiss).
 *
 * Use [None] to skip the animation and remove the card instantly.
 * Each exit direction is configured independently via [CuteCardConfig].
 */
enum class ExitAnimation {
    SlideUp,
    SlideDown,
    ScaleFade,
    Fade,
    None
}

/**
 * Controls all behavior and animation of [CuteCard].
 * Visual appearance is controlled separately via [CuteCardStyle].
 *
 * All timing values are in milliseconds.
 *
 * @param frontSide Which face is shown first. See [CardFrontSide].
 * @param flipDurationMs Duration of the 3D flip animation.
 * @param settledLockDurationMs How long the card ignores taps after flipping.
 * Prevents accidental double-taps from triggering "I know".
 * @param exitDurationMs Duration of the exit animation (confirm or dismiss).
 * @param flipDirection Axis of the flip. See [FlipDirection].
 * @param confirmExit Animation when the card is marked as known. See [ExitAnimation].
 * @param dismissExit Animation when the card is marked as unknown. See [ExitAnimation].
 */
@Immutable
data class CuteCardConfig(
    val frontSide: CardFrontSide = CardFrontSide.Word,
    val flipDurationMs: Int = 400,
    val settledLockDurationMs: Int = 350,
    val exitDurationMs: Int = 300,
    val flipDirection: FlipDirection = FlipDirection.Horizontal,
    val confirmExit: ExitAnimation = ExitAnimation.SlideUp,
    val dismissExit: ExitAnimation = ExitAnimation.SlideDown
)
