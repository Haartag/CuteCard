package site.llinsoft.cutecard.internal.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import site.llinsoft.cutecard.DismissButtonStyle
import site.llinsoft.cutecard.CuteCardLabels
import site.llinsoft.cutecard.internal.theme.CuteCardTokens
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Default Unflip button rendered at the top-right of the card area.
 * Displays a circular rewind arrow icon (stroke only, no fill).
 *
 * @param style  Visual appearance — uses [DismissButtonStyle.contentColor] for the icon tint.
 */
@Composable
internal fun UnflipButton(
    onClick: () -> Unit,
    style: DismissButtonStyle,
    labels: CuteCardLabels,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(CuteCardTokens.UnflipButtonTapSize)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = labels.unflipButtonLabel
            }
    ) {
        RewindIcon(
            tint = style.contentColor,
            strokeWidthDp = CuteCardTokens.UnflipIconStrokeWidth,
            modifier = Modifier.size(CuteCardTokens.UnflipIconSize)
        )
    }
}

/**
 * Circular rewind arrow drawn as an outline (no fill).
 *
 * The arc runs counterclockwise on screen from lower-left (210°) to the top (270°),
 * spanning 300°. An arrowhead at the top points left, indicating "go back".
 */
@Composable
private fun RewindIcon(
    tint: Color,
    strokeWidthDp: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val paint = Paint().apply {
            color = tint
            style = PaintingStyle.Stroke
            strokeWidth = strokeWidthDp.toPx()
            strokeCap = StrokeCap.Round
            strokeJoin = StrokeJoin.Round
        }

        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.width * 0.37f

        drawContext.canvas.apply {
            // Arc: start at 210° (lower-left), sweep -300° CCW on screen, end at 270° (top)
            drawArc(
                left   = cx - r,
                top    = cy - r,
                right  = cx + r,
                bottom = cy + r,
                startAngle = 210f,
                sweepAngle = -300f,
                useCenter  = false,
                paint      = paint
            )

            // Arrowhead at top (270° = 12-o'clock), pointing LEFT.
            // Tip: (cx, cy - r). Motion direction: LEFT (-x).
            // Wings spread ±35° from the backward direction (RIGHT = 0°):
            //   wing 1: 35° → (cos 35°,  sin 35°)
            //   wing 2: -35° → (cos 35°, -sin 35°)
            val tipX = cx
            val tipY = cy - r
            val arrowLen = r * 0.5f
            val wingAngle = (35.0 * PI / 180.0).toFloat()

            val arrowPath = Path().apply {
                moveTo(tipX + arrowLen * cos(wingAngle),  tipY + arrowLen * sin(wingAngle))
                lineTo(tipX, tipY)
                lineTo(tipX + arrowLen * cos(wingAngle),  tipY - arrowLen * sin(wingAngle))
            }
            drawPath(arrowPath, paint)
        }
    }
}
