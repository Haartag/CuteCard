package com.llinsoft.cutecard.internal.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.llinsoft.cutecard.api.DismissButtonStyle
import com.llinsoft.cutecard.api.CuteCardLabels
import com.llinsoft.cutecard.internal.theme.CuteCardTokens

/**
 * Default Dismiss button rendered below the card ("I don't know" case).
 *
 * @param style  Visual appearance from [CuteCardStyle.dismissButtonStyle].
 */
@Composable
internal fun DismissButton(
    onClick: () -> Unit,
    style: DismissButtonStyle,
    labels: CuteCardLabels,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .clip(style.shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .semantics { role = Role.Button }
            .padding(
                horizontal = CuteCardTokens.DismissButtonPaddingHorizontal,
                vertical = CuteCardTokens.DismissButtonPaddingVertical
            )
    ) {
        Text(
            text = labels.dismissButtonLabel,
            style = style.textStyle,
            color = style.contentColor,
            textAlign = TextAlign.Center
        )
    }
}
