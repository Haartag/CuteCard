package site.llinsoft.cutecard.internal.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import site.llinsoft.cutecard.CardShadow

internal fun CardShadow.toShadow(): Shadow = Shadow(
    radius = blur,
    color = color,
    offset = DpOffset(x = 0.dp, y = offsetY),
    spread = spread
)

/** Draws [shadows] behind the content, first layer lowest. */
internal fun Modifier.dropShadows(shadows: List<Shadow>, shape: Shape): Modifier =
    shadows.fold(this) { modifier, shadow -> modifier.dropShadow(shape = shape, shadow = shadow) }
