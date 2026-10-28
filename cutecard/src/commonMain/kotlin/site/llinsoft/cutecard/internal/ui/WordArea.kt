package site.llinsoft.cutecard.internal.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import site.llinsoft.cutecard.CuteCardStyle
import site.llinsoft.cutecard.WordVerticalPosition

/**
 * Lays out the main word of a card face inside the area left after
 * [CuteCardStyle.contentPadding] and the reserved top / bottom rows.
 *
 * Both faces go through this so the same style settings place the word identically.
 *
 * @param details Optional content shown directly below the word (phonetics, word class).
 * With [WordVerticalPosition.Centered] it never shifts the word.
 */
@Composable
internal fun WordArea(
    style: CuteCardStyle,
    word: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    details: (@Composable () -> Unit)? = null
) {
    val areaModifier = modifier
        .fillMaxSize()
        .padding(style.contentPadding)
        .padding(top = style.reservedTopHeight, bottom = style.reservedBottomHeight)

    when (style.wordVerticalPosition) {
        WordVerticalPosition.Raised -> Column(
            modifier = areaModifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.4f))
            word()
            if (details != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { details() }
            }
            Spacer(Modifier.weight(0.6f))
        }

        WordVerticalPosition.Centered -> Layout(
            modifier = areaModifier,
            content = {
                word()
                Column(horizontalAlignment = Alignment.CenterHorizontally) { details?.invoke() }
            }
        ) { measurables, constraints ->
            val loose = constraints.copy(minWidth = 0, minHeight = 0)
            val detailsPlaceable = measurables[1].measure(loose)
            // The details hang below a centred word, so the word may only use the
            // height that keeps an equal-sized gap above it — otherwise the details
            // would be pushed out of the area. Auto-size shrinks the word to fit.
            val wordMaxHeight = (constraints.maxHeight - 2 * detailsPlaceable.height).coerceAtLeast(0)
            val wordPlaceable = measurables[0].measure(loose.copy(maxHeight = wordMaxHeight))

            layout(constraints.maxWidth, constraints.maxHeight) {
                val wordY = (constraints.maxHeight - wordPlaceable.height) / 2
                wordPlaceable.place((constraints.maxWidth - wordPlaceable.width) / 2, wordY)
                detailsPlaceable.place(
                    (constraints.maxWidth - detailsPlaceable.width) / 2,
                    wordY + wordPlaceable.height
                )
            }
        }
    }
}
