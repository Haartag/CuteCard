package site.llinsoft.cutecard.internal.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import site.llinsoft.cutecard.CuteCardContent
import site.llinsoft.cutecard.CuteCardLabels
import site.llinsoft.cutecard.CuteCardStyle
import site.llinsoft.cutecard.internal.theme.CuteCardTokens

/**
 * Back face of [CuteCard] - the "full info" side.
 *
 * @param isPlaying         Whether audio is currently playing. Drives audio button state.
 * @param onAudioRequested  Null hides the audio button entirely.
 */
@Composable
internal fun CardBack(
    content: CuteCardContent,
    style: CuteCardStyle,
    labels: CuteCardLabels,
    isPlaying: Boolean,
    onAudioRequested: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(CuteCardTokens.CardContentPadding)
                .semantics {
                    contentDescription = labels.cardBackContentDescription
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.4f))
            Text(
                text = content.translation,
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

            if (content.phonetics != null) {
                Text(
                    text = content.phonetics,
                    style = style.phoneticsTextStyle,
                    color = style.phoneticsTextColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(
                        top = CuteCardTokens.WordToPhoneticsSpacing
                    )
                )
            }

            if (content.wordClass != null) {
                WordClassPill(
                    wordClass = content.wordClass,
                    style = style,
                    modifier = Modifier.padding(
                        top = if (content.phonetics != null)
                            CuteCardTokens.PhoneticsToWordClassSpacing
                        else
                            CuteCardTokens.WordToPhoneticsSpacing
                    )
                )
            }
            Spacer(Modifier.weight(0.6f))
        }

        if (onAudioRequested != null) {
            AudioButton(
                isPlaying = isPlaying,
                onClick = onAudioRequested,
                style = style.audioButtonStyle,
                labels = labels,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = CuteCardTokens.CardContentPadding,
                        end = CuteCardTokens.CardContentPadding,
                        bottom = CuteCardTokens.CardContentPadding
                    )
            )
        }

        if (content.targetLanguage != null) {
            val backPillStyle = style.languagePillStyle.let { s ->
                if (s.backTextColor == null && s.backContainerColor == null) s
                else s.copy(
                    textColor = s.backTextColor ?: s.textColor,
                    containerColor = s.backContainerColor ?: s.containerColor
                )
            }
            LanguagePill(
                language = content.targetLanguage,
                style = backPillStyle,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(style.languagePillStyle.cornerPadding)
            )
        }
    }
}
