package com.llinsoft.cutecard.api

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.llinsoft.cutecard.internal.theme.CuteCardTokens

/**
 * Default values for [CuteCard].
 *
 * Use these as a starting point and override only what you need via [copy]:
 *
 * ```kotlin
 * CuteCard(
 *     style = CuteCardDefaults.style().copy(
 *         cardBackgroundColor = Color(0xFF1E1E1E)
 *     )
 * )
 * ```
 */
object CuteCardDefaults {

    /**
     * Default [CuteCardConfig]. Override individual fields via [copy].
     */
    fun config(): CuteCardConfig = CuteCardConfig()

    /**
     * Default [CuteCardLabels]. Ships in English.
     * Replace any field to localise or adjust tone.
     */
    fun labels(): CuteCardLabels = CuteCardLabels()

    /**
     * Default [CuteCardStyle] using the library's built-in design tokens.
     * Light-mode oriented. For dark mode use [darkStyle].
     */
    @Composable
    fun style(): CuteCardStyle = CuteCardStyle(

        // Card surface

        cardShape = RoundedCornerShape(CuteCardTokens.CardCornerRadius),
        cardElevation = CuteCardTokens.CardElevation,
        cardAspectRatio = CuteCardTokens.CardAspectRatio,
        cardBackgroundColor = Color(0xFFFAFAF8),
        ghostCardBackgroundColor = Color(0xFFF0F0EE),
        cardBorderColor = Color(0x1A000000),

        // Word / translation

        wordTextStyle = TextStyle(
            fontSize = CuteCardTokens.WordTextSize,
            fontWeight = FontWeight.Medium
        ),
        wordTextColor = Color(0xFF1A1A1A),

        // Phonetics

        phoneticsTextStyle = TextStyle(
            fontSize = CuteCardTokens.PhoneticsTextSize,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Italic
        ),
        phoneticsTextColor = Color(0xFF888888),

        // Word class pill

        wordClassPillStyle = WordClassPillStyle(
            textStyle = TextStyle(
                fontSize = CuteCardTokens.WordClassTextSize,
                fontWeight = FontWeight.Normal,
                letterSpacing = CuteCardTokens.WordClassTextSize * 0.04f
            ),
            textColor = Color(0xFF888888),
            containerColor = Color(0x0F000000),
            borderColor = Color(0x1A000000),
            shape = RoundedCornerShape(CuteCardTokens.WordClassPillCornerRadius)
        ),

        // Audio button

        audioButtonStyle = AudioButtonStyle(
            idleIconColor = Color(0xFF888888),
            playingIconColor = Color(0xFF1D9E75),
            idleStrokeWidth = CuteCardTokens.AudioIconIdleStrokeWidth,
            playingStrokeWidth = CuteCardTokens.AudioIconPlayingStrokeWidth,
            containerColor = Color(0x0F000000),
            contentColor = Color(0xFF888888),
            shape = RoundedCornerShape(CuteCardTokens.AudioButtonCornerRadius),
            textStyle = TextStyle(
                fontSize = CuteCardTokens.PhoneticsTextSize,
                fontWeight = FontWeight.Normal
            )
        ),

        // Dismiss button

        dismissButtonStyle = DismissButtonStyle(
            contentColor = Color(0xFF888888),
            textStyle = TextStyle(
                fontSize = CuteCardTokens.PhoneticsTextSize,
                fontWeight = FontWeight.Normal
            ),
            shape = RoundedCornerShape(50)
        )
    )

    /**
     * Convenience dark mode style. Mirrors [style] with adjusted surface
     * and text colors for dark backgrounds.
     */
    @Composable
    fun darkStyle(): CuteCardStyle {
        val base = style()
        return base.copy(
            cardBackgroundColor = Color(0xFF1E1E1E),
            ghostCardBackgroundColor = Color(0xFF161616),
            cardBorderColor = Color(0x1AFFFFFF),
            wordTextColor = Color(0xFFF0EFE9),
            phoneticsTextColor = Color(0xFF888888),
            wordClassPillStyle = base.wordClassPillStyle.copy(
                textColor = Color(0xFF888888),
                containerColor = Color(0x0FFFFFFF),
                borderColor = Color(0x1AFFFFFF)
            ),
            audioButtonStyle = base.audioButtonStyle.copy(
                containerColor = Color(0x0FFFFFFF),
                contentColor = Color(0xFF888888),
                idleIconColor = Color(0xFF888888)
            ),
            dismissButtonStyle = base.dismissButtonStyle.copy(
                contentColor = Color(0xFF666666)
            )
        )
    }
}
