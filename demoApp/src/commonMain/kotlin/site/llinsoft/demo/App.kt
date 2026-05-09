package site.llinsoft.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import site.llinsoft.cutecard.CuteCard
import site.llinsoft.cutecard.CuteCardContent

@Composable
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DemoContent()
        }
    }
}

@Composable
private fun DemoContent() {
    val initialDeck = remember { SampleDeck.cards }
    var deck by remember { mutableStateOf(initialDeck) }
    var knownCount by remember { mutableIntStateOf(0) }
    var cardKey by remember { mutableIntStateOf(0) }

    if (deck.isEmpty()) {
        CompletionScreen(
            knownCount = knownCount,
            total = initialDeck.size,
            onRestart = {
                deck = initialDeck
                knownCount = 0
                cardKey++
            }
        )
    } else {
        DeckScreen(
            card = deck.first(),
            cardKey = cardKey,
            knownCount = knownCount,
            remaining = deck.size,
            total = initialDeck.size,
            onKnown = {
                deck = deck.drop(1)
                knownCount++
                cardKey++
            },
            onUnknown = {
                // Move current card to the back of the deck so it's seen again
                deck = deck.drop(1) + listOf(deck.first())
                cardKey++
            }
        )
    }
}

@Composable
private fun DeckScreen(
    card: CuteCardContent,
    cardKey: Int,
    knownCount: Int,
    remaining: Int,
    total: Int,
    onKnown: () -> Unit,
    onUnknown: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Header ───────────────────────────────────────────────────────────

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "CuteCard",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = "$knownCount / $total known",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        LinearProgressIndicator(
            progress = { knownCount.toFloat() / total },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // ── Card ─────────────────────────────────────────────────────────────
        // key(cardKey) remounts CuteCard fresh for each new card,
        // even when the CuteCardContent value is the same object (e.g. retry loop).

        key(cardKey) {
            CuteCard(
                content = card,
                onKnown = onKnown,
                onUnknown = onUnknown
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "$remaining card${if (remaining == 1) "" else "s"} remaining",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun CompletionScreen(
    knownCount: Int,
    total: Int,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$knownCount / $total",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "words known",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (knownCount == total) "Perfect round!" else "Keep going — you'll get there.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = onRestart) {
            Text("Practice Again")
        }
    }
}
