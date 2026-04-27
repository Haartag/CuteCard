package com.llinsoft.cutecard.internal.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.dp
import com.llinsoft.cutecard.api.CuteCardContent
import com.llinsoft.cutecard.api.CuteCardLabels
import com.llinsoft.cutecard.api.CuteCardStyle
import com.llinsoft.cutecard.internal.theme.CuteCardTokens

/** Front face of [CuteCard] - always renders the word only. */
@Composable
internal fun CardFront(
    content: CuteCardContent,
    style: CuteCardStyle,
    labels: CuteCardLabels,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CuteCardTokens.CardContentPadding)
            .semantics {
                contentDescription = labels.cardFrontContentDescription
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = content.word,
            style = style.wordTextStyle,
            color = style.wordTextColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Pill-shaped chip displaying the grammatical word class e.g. "noun", "verb", "adjective". */
@Composable
internal fun WordClassPill(
    wordClass: String,
    style: CuteCardStyle,
    modifier: Modifier = Modifier
) {
    val pillStyle = style.wordClassPillStyle

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(pillStyle.shape)
            .background(pillStyle.containerColor)
            .border(
                width = 0.5.dp,
                color = pillStyle.borderColor,
                shape = pillStyle.shape
            )
            .padding(
                horizontal = CuteCardTokens.WordClassPillPaddingHorizontal,
                vertical = CuteCardTokens.WordClassPillPaddingVertical
            )
    ) {
        Text(
            text = wordClass,
            style = pillStyle.textStyle,
            color = pillStyle.textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
