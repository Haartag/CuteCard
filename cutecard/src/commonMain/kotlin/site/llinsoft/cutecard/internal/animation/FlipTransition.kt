package site.llinsoft.cutecard.internal.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import site.llinsoft.cutecard.CuteCardConfig
import site.llinsoft.cutecard.FlipDirection
import site.llinsoft.cutecard.internal.state.CuteCardState
import site.llinsoft.cutecard.internal.state.isBackVisible

/**
 * 3D flip animation for [CuteCard].
 *
 * A card flip is two sequential half-rotations:
 *  1. Front face rotates from 0° → 90° (disappears edge-on).
 *  2. Back face rotates from 90° → 0° (comes into view).
 *
 * Each face receives its own [Modifier] via [frontModifier] and [backModifier].
 * The caller applies these to the front and back composables respectively.
 *
 * [onFlipFinished] is called once the full flip animation completes —
 * the state holder uses this to transition from [CuteCardState.Flipping]
 * to [CuteCardState.Settling].
 *
 * @param state Current card state. Drives when the flip starts.
 * @param config Consumer config. Provides duration and direction.
 * @param onFlipFinished Called when the flip animation completes.
 */
@Composable
internal fun rememberFlipTransition(
    state: CuteCardState,
    config: CuteCardConfig,
    onFlipFinished: () -> Unit
): FlipTransition {
    val isFlipped = state is CuteCardState.Flipping || state.isBackVisible

    var targetRotation by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isFlipped) {
        targetRotation = if (isFlipped) 180f else 0f
    }

    val rotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = if (targetRotation == 180f)
            CuteCardAnimator.flipSpec(config)
        else
            snap(),
        label = "card_flip_rotation",
        finishedListener = { finalValue ->
            if (finalValue == 180f) onFlipFinished()
        }
    )

    return remember(rotation, config.flipDirection) {
        FlipTransition(rotation = rotation, direction = config.flipDirection)
    }
}

/**
 * Holds the computed [Modifier]s for the front and back card faces.
 *
 * - [frontModifier] applies the rotation and hides the face when rotation > 90°.
 * - [backModifier] applies the counter-rotation and hides the face when rotation ≤ 90°.
 */
internal class FlipTransition(
    private val rotation: Float,
    private val direction: FlipDirection
) {
    // high enough to prevent perspective distortion?
    private val cameraDistance = 8f

    val frontModifier: Modifier
        get() = Modifier.graphicsLayer {
            setCameraDistance(cameraDistance)
            applyRotation(rotation)
            alpha = if (rotation > 90f) 0f else 1f
        }

    val backModifier: Modifier
        get() = Modifier.graphicsLayer {
            setCameraDistance(cameraDistance)
            applyRotation(rotation - 180f)
            alpha = if (rotation <= 90f) 0f else 1f
        }

    private fun GraphicsLayerScope.setCameraDistance(distance: Float) {
        cameraDistance = density * distance
    }

    private fun GraphicsLayerScope.applyRotation(degrees: Float) {
        when (direction) {
            FlipDirection.Horizontal -> rotationY = degrees
            FlipDirection.Vertical   -> rotationX = degrees
        }
    }
}
