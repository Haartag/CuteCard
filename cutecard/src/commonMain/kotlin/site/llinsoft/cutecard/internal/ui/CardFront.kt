package site.llinsoft.cutecard.internal.ui

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
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.dp
import site.llinsoft.cutecard.CuteCardContent
import site.llinsoft.cutecard.CuteCardLabels
import site.llinsoft.cutecard.CuteCardStyle
import site.llinsoft.cutecard.LanguagePillStyle
import site.llinsoft.cutecard.internal.theme.CuteCardTokens

/** Front face of [CuteCard] - always renders the word only. */
@Composable
internal fun CardFront(
    content: CuteCardContent,
    style: CuteCardStyle,
    labels: CuteCardLabels,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
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
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = style.wordAutoSizeMinFontSize,
                    maxFontSize = style.wordTextStyle.fontSize,
                ),
            )
        }

        if (content.sourceLanguage != null) {
            LanguagePill(
                language = content.sourceLanguage,
                style = style.languagePillStyle,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(style.languagePillStyle.cornerPadding)
            )
        }
    }
}

/** Small pill showing a 2–3 character language code e.g. "EN", "RU" in the card corner. */
@Composable
internal fun LanguagePill(
    language: String,
    style: LanguagePillStyle,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(style.shape)
            .background(style.containerColor)
            .padding(
                horizontal = style.paddingHorizontal,
                vertical = style.paddingVertical
            )
    ) {
        Text(
            text = language.uppercase(),
            style = style.textStyle,
            color = style.textColor,
            maxLines = 1
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
