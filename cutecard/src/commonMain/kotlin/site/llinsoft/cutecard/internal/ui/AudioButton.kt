package site.llinsoft.cutecard.internal.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import site.llinsoft.cutecard.api.AudioButtonStyle
import site.llinsoft.cutecard.api.CuteCardLabels
import site.llinsoft.cutecard.internal.theme.CuteCardTokens

/**
 * Audio playback button rendered on the full-info face of [CuteCard].
 *
 * Displays two visual states driven by [isPlaying]:
 * - Idle — muted icon color, regular stroke weight, idle label.
 * - Playing — accent icon color, thicker stroke, playing label.
 *
 * @param isPlaying  Whether audio is currently playing. Drives visual state.
 * @param style      Visual appearance from [CuteCardStyle.audioButtonStyle].
 */
@Composable
internal fun AudioButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    style: AudioButtonStyle,
    labels: CuteCardLabels,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconColor by animateColorAsState(
        targetValue = if (isPlaying) style.playingIconColor else style.idleIconColor,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = CuteCardTokens.AudioButtonTransitionDurationMs
        ),
        label = "audio_icon_color"
    )

    val strokeWidth by animateFloatAsState(
        targetValue = if (isPlaying)
            style.playingStrokeWidth.value
        else
            style.idleStrokeWidth.value,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = CuteCardTokens.AudioButtonTransitionDurationMs
        ),
        label = "audio_icon_stroke_width"
    )

    val label = if (isPlaying) labels.audioButtonPlayingLabel else labels.audioButtonIdleLabel

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(style.shape)
            .background(style.containerColor)
            .border(width = 0.5.dp, color = style.idleIconColor.copy(alpha = 0.2f), shape = style.shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            )
            .padding(vertical = CuteCardTokens.AudioButtonPaddingVertical)
            .semantics {
                contentDescription = labels.audioButtonContentDescription
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpeakerIcon(
            tint = iconColor,
            strokeWidth = strokeWidth,
            modifier = Modifier.size(CuteCardTokens.AudioIconSize)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = style.textStyle,
            color = if (isPlaying) style.playingIconColor else style.contentColor,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Speaker / audio icon drawn as a vector with a dynamic stroke width.
 * Using a custom drawn icon allows the stroke width to animate smoothly
 * between idle and playing states.
 */
@Composable
private fun SpeakerIcon(
    tint: Color,
    strokeWidth: Float,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        drawContext.canvas.apply {
            val paint = androidx.compose.ui.graphics.Paint().apply {
                this.color = tint
                this.style = androidx.compose.ui.graphics.PaintingStyle.Stroke
                this.strokeWidth = strokeWidth * density
                this.strokeCap = StrokeCap.Round
            }
            val sx = size.width / 24f
            val sy = size.height / 24f

            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(11f * sx, 5f * sy)
                lineTo(6f * sx, 9f * sy)
                lineTo(2f * sx, 9f * sy)
                lineTo(2f * sx, 15f * sy)
                lineTo(6f * sx, 15f * sy)
                lineTo(11f * sx, 19f * sy)
                close()
            }
            drawPath(path, paint)

            drawArc(
                left = 13f * sx,
                top = 4.93f * sy,
                right = 23f * sx,
                bottom = 19.07f * sy,
                startAngle = -37f,
                sweepAngle = 74f,
                useCenter = false,
                paint = paint
            )

            drawArc(
                left = 13.5f * sx,
                top = 8.46f * sy,
                right = 20.5f * sx,
                bottom = 15.54f * sy,
                startAngle = -45f,
                sweepAngle = 90f,
                useCenter = false,
                paint = paint
            )
        }
    }
}
