package site.llinsoft.cutecard.internal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import site.llinsoft.cutecard.CuteCardStyle
import site.llinsoft.cutecard.internal.theme.CuteCardTokens

/**
 * Decorative ghost cards behind the active card to create a physical deck illusion.
 *
 * Ghost cards have no interaction — they are purely decorative.
 *
 * @param style       Consumer style — provides card shape, background, border color and
 *                    [CuteCardStyle.ghostStackStyle] geometry.
 * @param ghostCount  Number of ghost cards to show (0, 1, or 2).
 */
@Composable
internal fun GhostStack(
    style: CuteCardStyle,
    ghostCount: Int,
    modifier: Modifier = Modifier
) {
    val ghost = style.ghostStackStyle
    Box(modifier = modifier) {
        if (ghostCount >= 2) {
            GhostCard(
                step = 2,
                defaultScale = CuteCardTokens.GhostCard3Scale,
                elevation = CuteCardTokens.GhostCard3Elevation,
                offsetY = ghost.offsetPerStep?.times(2) ?: CuteCardTokens.GhostCard3OffsetY,
                alpha = ghost.farCardAlpha,
                style = style,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        if (ghostCount >= 1) {
            GhostCard(
                step = 1,
                defaultScale = CuteCardTokens.GhostCard2Scale,
                elevation = CuteCardTokens.GhostCard2Elevation,
                offsetY = ghost.offsetPerStep ?: CuteCardTokens.GhostCard2OffsetY,
                alpha = ghost.nearCardAlpha,
                style = style,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun GhostCard(
    step: Int,
    defaultScale: Float,
    elevation: Dp,
    offsetY: Dp,
    alpha: Float,
    style: CuteCardStyle,
    modifier: Modifier = Modifier
) {
    val shape = style.cardShape
    val sideInset = style.ghostStackStyle.sideInsetPerStep
    val size = if (sideInset == null) {
        Modifier
            .fillMaxWidth(defaultScale)
            .aspectRatio(style.cardAspectRatio)
    } else {
        Modifier
            .fillMaxSize()
            .padding(horizontal = sideInset * step)
    }

    // Outer box owns the shadow so it sits outside the alpha layer.
    // Prevents the alpha graphicsLayer from clipping the shadow at card bounds.
    Box(
        modifier = modifier
            .then(size)
            .offset(y = offsetY)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = elevation * 1.6f,
                    color = Color.Black,
                    alpha = alpha * 0.18f,
                    offset = DpOffset(x = 0.dp, y = elevation * 0.35f),
                    spread = 0.dp
                )
            )
    ) {
        // Inner box applies alpha as a group so border and background composite
        // correctly as a unit rather than individually against the parent.
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(alpha)
                .clip(shape)
                .background(style.ghostCardBackgroundColor)
                .border(
                    width = 0.5.dp,
                    color = style.cardBorderColor,
                    shape = shape
                )
        )
    }
}
