package com.llinsoft.cutecard.internal.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import com.llinsoft.cutecard.api.CuteCardConfig
import com.llinsoft.cutecard.api.ExitAnimation

/**
 * Translates [CuteCardConfig] timing values into Compose [AnimationSpec] instances.
 *
 * Easing choices:
 * - Flip uses [FastOutSlowInEasing] - accelerates into the flip, decelerates out.
 *   Like a card being flicked.
 * - SlideUp / SlideDown use [LinearOutSlowInEasing] - slide.
 * - ScaleFade uses [FastOutSlowInEasing] - smooth fade.
 * - Fade uses [LinearEasing] - opacity fades, no easing.
 */
internal object CuteCardAnimator {

    // Flip

    /**
     * Two-phase keyframes spec for the full 0° → 180° flip.
     * First phase (0°→90°) uses [FastOutSlowInEasing] - the front face flicks away.
     * Second phase (90°→180°) uses [LinearOutSlowInEasing] - the back face sweeps in.
     * Total duration equals [CuteCardConfig.flipDurationMs].
     */
    fun flipSpec(config: CuteCardConfig): AnimationSpec<Float> = keyframes {
        durationMillis = config.flipDurationMs
        0f at 0 using FastOutSlowInEasing
        90f at (config.flipDurationMs * 0.4).toInt() using LinearOutSlowInEasing
        180f at config.flipDurationMs
    }

    // Exit - SlideUp

    fun slideUpSpec(config: CuteCardConfig): AnimationSpec<Float> = tween(
        durationMillis = config.exitDurationMs,
        easing = LinearOutSlowInEasing
    )

    /**
     * AnimationSpec for the [ExitAnimation.SlideUp] fade component.
     * Slightly shorter than the translation - card fades before fully off-screen.
     */
    fun slideUpFadeSpec(config: CuteCardConfig): AnimationSpec<Float> = tween(
        durationMillis = (config.exitDurationMs * 0.75f).toInt(),
        easing = LinearEasing
    )

    // Exit - SlideDown

    fun slideDownSpec(config: CuteCardConfig): AnimationSpec<Float> = tween(
        durationMillis = config.exitDurationMs,
        easing = LinearOutSlowInEasing
    )

    fun slideDownFadeSpec(config: CuteCardConfig): AnimationSpec<Float> = tween(
        durationMillis = (config.exitDurationMs * 0.75f).toInt(),
        easing = LinearEasing
    )

    // Exit - ScaleFade

    /**
     * AnimationSpec for the [ExitAnimation.ScaleFade] scale component.
     * Card scales up slightly before fading out.
     */
    fun scaleFadeScaleSpec(config: CuteCardConfig): AnimationSpec<Float> = tween(
        durationMillis = config.exitDurationMs,
        easing = FastOutSlowInEasing
    )

    fun scaleFadeAlphaSpec(config: CuteCardConfig): AnimationSpec<Float> = tween(
        durationMillis = config.exitDurationMs,
        easing = FastOutSlowInEasing
    )

    // Exit - Fade

    fun fadeSpec(config: CuteCardConfig): AnimationSpec<Float> = tween(
        durationMillis = config.exitDurationMs,
        easing = LinearEasing
    )

}
