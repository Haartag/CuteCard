package site.llinsoft.cutecard.internal.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import site.llinsoft.cutecard.ExitAnimation
import site.llinsoft.cutecard.CuteCardConfig
import site.llinsoft.cutecard.internal.state.CuteCardState

/**
 * Slide offset expressed as a fraction of the card's own height.
 * 1.0f = card slides exactly one full card-height up or down.
 */
private const val SlideOffsetFraction = 1.2f

/**
 * Scale target for [ExitAnimation.ScaleFade].
 * Card grows slightly before fading.
 */
private const val ScaleFadeTargetScale = 1.08f

/**
 * Builds and returns the correct exit [Modifier] for the active card
 * based on [CuteCardConfig.confirmExit] or [CuteCardConfig.dismissExit].
 *
 * The modifier drives translation, alpha, and scale animations
 * via [animateFloatAsState]. Each animation is keyed to [isExiting]
 * so it triggers the moment the state transitions to an exiting state.
 *
 * [onExitFinished] is called once the exit animation settles —
 * the state holder uses this to transition to [CuteCardState.Gone].
 *
 * @param state Current card state.
 * @param config Consumer config — provides exit animation choice and duration.
 * @param onExitFinished Called when the exit animation completes.
 */
@Composable
internal fun exitModifier(
    state: CuteCardState,
    config: CuteCardConfig,
    onExitFinished: () -> Unit
): Modifier {
    val isExitingConfirm = state is CuteCardState.ExitingConfirm
    val isExitingDismiss = state is CuteCardState.ExitingDismiss
    val isExiting = isExitingConfirm || isExitingDismiss

    // Always call both so each Animatable initialises at rest before an exit triggers —
    // if first seen at the exit target it skips the animation and finishedListener never fires.
    // Immutable config values keep composable call order stable.
    val confirmMod = when (config.confirmExit) {
        ExitAnimation.SlideUp   -> slideUpModifier(isExitingConfirm, config, onExitFinished)
        ExitAnimation.SlideDown -> slideDownModifier(isExitingConfirm, config, onExitFinished)
        ExitAnimation.ScaleFade -> scaleFadeModifier(isExitingConfirm, config, onExitFinished)
        ExitAnimation.Fade      -> fadeModifier(isExitingConfirm, config, onExitFinished)
        ExitAnimation.None      -> noneModifier(isExitingConfirm, onExitFinished)
    }
    val dismissMod = when (config.dismissExit) {
        ExitAnimation.SlideUp   -> slideUpModifier(isExitingDismiss, config, onExitFinished)
        ExitAnimation.SlideDown -> slideDownModifier(isExitingDismiss, config, onExitFinished)
        ExitAnimation.ScaleFade -> scaleFadeModifier(isExitingDismiss, config, onExitFinished)
        ExitAnimation.Fade      -> fadeModifier(isExitingDismiss, config, onExitFinished)
        ExitAnimation.None      -> noneModifier(isExitingDismiss, onExitFinished)
    }

    return when {
        isExitingConfirm -> confirmMod
        isExitingDismiss -> dismissMod
        else             -> Modifier
    }
}

// SlideUp

@Composable
private fun slideUpModifier(
    isExiting: Boolean,
    config: CuteCardConfig,
    onExitFinished: () -> Unit
): Modifier {
    val offsetFraction by animateFloatAsState(
        targetValue = if (isExiting) -SlideOffsetFraction else 0f,
        animationSpec = CuteCardAnimator.slideUpSpec(config),
        label = "exit_slide_up_offset",
        finishedListener = {
            if (it == -SlideOffsetFraction) onExitFinished()
        }
    )

    val alpha by animateFloatAsState(
        targetValue = if (isExiting) 0f else 1f,
        animationSpec = CuteCardAnimator.slideUpFadeSpec(config),
        label = "exit_slide_up_alpha"
    )

    return Modifier
        .graphicsLayer { translationY = offsetFraction * size.height }
        .alpha(alpha)
}

// SlideDown

@Composable
private fun slideDownModifier(
    isExiting: Boolean,
    config: CuteCardConfig,
    onExitFinished: () -> Unit
): Modifier {
    val offsetFraction by animateFloatAsState(
        targetValue = if (isExiting) SlideOffsetFraction else 0f,
        animationSpec = CuteCardAnimator.slideDownSpec(config),
        label = "exit_slide_down_offset",
        finishedListener = { if (it == SlideOffsetFraction) onExitFinished() }
    )

    val alpha by animateFloatAsState(
        targetValue = if (isExiting) 0f else 1f,
        animationSpec = CuteCardAnimator.slideDownFadeSpec(config),
        label = "exit_slide_down_alpha"
    )

    return Modifier
        .graphicsLayer { translationY = offsetFraction * size.height }
        .alpha(alpha)
}

// ScaleFade

@Composable
private fun scaleFadeModifier(
    isExiting: Boolean,
    config: CuteCardConfig,
    onExitFinished: () -> Unit
): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (isExiting) ScaleFadeTargetScale else 1f,
        animationSpec = CuteCardAnimator.scaleFadeScaleSpec(config),
        label = "exit_scale_fade_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isExiting) 0f else 1f,
        animationSpec = CuteCardAnimator.scaleFadeAlphaSpec(config),
        label = "exit_scale_fade_alpha",
        finishedListener = { if (it == 0f) onExitFinished() }
    )

    return Modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .alpha(alpha)
}

// Fade

@Composable
private fun fadeModifier(
    isExiting: Boolean,
    config: CuteCardConfig,
    onExitFinished: () -> Unit
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (isExiting) 0f else 1f,
        animationSpec = CuteCardAnimator.fadeSpec(config),
        label = "exit_fade_alpha",
        finishedListener = { if (it == 0f) onExitFinished() }
    )

    return Modifier.alpha(alpha)
}

// None

@Composable
private fun noneModifier(isExiting: Boolean, onExitFinished: () -> Unit): Modifier {
    if (isExiting) SideEffect { onExitFinished() }
    return Modifier
}
