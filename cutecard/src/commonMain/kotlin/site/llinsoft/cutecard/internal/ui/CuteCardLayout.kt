package site.llinsoft.cutecard.internal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import site.llinsoft.cutecard.CardFrontSide
import site.llinsoft.cutecard.CuteCardConfig
import site.llinsoft.cutecard.CuteCardContent
import site.llinsoft.cutecard.CuteCardLabels
import site.llinsoft.cutecard.CuteCardStyle
import site.llinsoft.cutecard.internal.animation.exitModifier
import site.llinsoft.cutecard.internal.animation.rememberFlipTransition
import site.llinsoft.cutecard.internal.state.CuteCardState
import site.llinsoft.cutecard.internal.state.CuteCardStateHolder
import site.llinsoft.cutecard.internal.state.isBackVisible
import site.llinsoft.cutecard.internal.state.isInteractionLocked
import site.llinsoft.cutecard.internal.theme.CuteCardTokens
import kotlinx.coroutines.delay

/**
 * Internal layout for [CuteCard]: ghost stack, flip transition, exit animation,
 * and face routing based on [CuteCardConfig.frontSide].
 *
 * @param onAudioRequested  Null hides the audio button.
 * @param onFlipped         Called when the back face becomes interactive (flip + settle done).
 * @param onFlippedBack     Called when the user taps the back face to confirm (before exit starts).
 * @param onKnown           Called after confirm exit animation completes.
 * @param onUnknown         Called after dismiss exit animation completes.
 * @param dismissButton     Slot shown below the card on the back face; must call its onClick to trigger exit.
 */
@Composable
internal fun CuteCardLayout(
    stateHolder: CuteCardStateHolder,
    content: CuteCardContent,
    config: CuteCardConfig,
    style: CuteCardStyle,
    labels: CuteCardLabels,
    isPlaying: Boolean,
    onAudioRequested: (() -> Unit)?,
    onFlipped: (() -> Unit)?,
    onFlippedBack: (() -> Unit)?,
    onKnown: () -> Unit,
    onUnknown: () -> Unit,
    dismissButton: @Composable (onClick: () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = stateHolder.state

    // ── Settle lock timer ────────────────────────────────────────────────────

    LaunchedEffect(stateHolder.shouldRunSettleTimer) {
        if (stateHolder.shouldRunSettleTimer) {
            delay(config.settledLockDurationMs.toLong())
            stateHolder.onSettled()
        }
    }

    // ── Flip callbacks ───────────────────────────────────────────────────────

    LaunchedEffect(state) {
        when (state) {
            CuteCardState.Back           -> onFlipped?.invoke()
            CuteCardState.ExitingConfirm -> onFlippedBack?.invoke()
            else                          -> {}
        }
    }

    // ── Flip transition ──────────────────────────────────────────────────────

    val flipTransition = rememberFlipTransition(
        state = state,
        config = config,
        onFlipFinished = stateHolder::onFlipAnimationFinished
    )

    // ── Exit modifier ────────────────────────────────────────────────────────

    val exit = exitModifier(
        state = state,
        config = config,
        onExitFinished = {
            when (stateHolder.state) {
                CuteCardState.ExitingConfirm -> onKnown()
                CuteCardState.ExitingDismiss -> onUnknown()
                else -> {}
            }
            stateHolder.onExitAnimationFinished()
        }
    )

    // ── Shadow elevation ─────────────────────────────────────────────────────
    // Hide 50 ms after flip starts; restore 50 ms before flip ends.

    var shadowElevation by remember { mutableStateOf(style.cardElevation) }
    LaunchedEffect(state) {
        if (state is CuteCardState.Flipping) {
            delay(50)
            shadowElevation = 0.dp
            delay((config.flipDurationMs - 100).toLong().coerceAtLeast(0))
            shadowElevation = style.cardElevation
        } else {
            shadowElevation = style.cardElevation
        }
    }

    // ── Determine which content goes on which face ───────────────────────────

    val frontIsWordOnly = config.frontSide == CardFrontSide.Word
    val showDismissButton = state.isBackVisible

    // ── Layout ───────────────────────────────────────────────────────────────

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = CuteCardTokens.CardHorizontalPadding)
        ) {
            GhostStack(
                style = style,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(style.cardAspectRatio)
            )

            Box(
                modifier = Modifier
                    .then(exit)
                    .fillMaxWidth()
                    .aspectRatio(style.cardAspectRatio)
                    .shadow(
                        elevation = shadowElevation,
                        shape = style.cardShape,
                        clip = false
                    )
                    .clickable(
                        enabled = !state.isInteractionLocked,
                        indication = null,
                        interactionSource = null,
                        onClick = stateHolder::onCardTap
                    )
            ) {
                // Each face gets its own graphicsLayer so the full surface
                // (clip, background, border) rotates — not just the text inside.
                Box(
                    modifier = flipTransition.frontModifier
                        .fillMaxSize()
                        .clip(style.cardShape)
                        .background(style.cardBackgroundColor)
                        .border(
                            width = 0.5.dp,
                            color = style.cardBorderColor,
                            shape = style.cardShape
                        )
                ) {
                    CardFrontFace(
                        frontIsWordOnly = frontIsWordOnly,
                        content = content,
                        style = style,
                        labels = labels,
                        isPlaying = isPlaying,
                        onAudioRequested = onAudioRequested
                    )
                }

                Box(
                    modifier = flipTransition.backModifier
                        .fillMaxSize()
                        .clip(style.cardShape)
                        .background(style.cardBackgroundColor)
                        .border(
                            width = 0.5.dp,
                            color = style.cardBorderColor,
                            shape = style.cardShape
                        )
                ) {
                    CardBackFace(
                        frontIsWordOnly = frontIsWordOnly,
                        content = content,
                        style = style,
                        labels = labels,
                        isPlaying = isPlaying,
                        onAudioRequested = onAudioRequested
                    )
                }
            }
        }

        if (showDismissButton) {
            Box(modifier = Modifier.padding(top = CuteCardTokens.DismissButtonTopPadding)) {
                dismissButton(stateHolder::onDismissTap)
            }
        } else {
            Spacer(
                modifier = Modifier
                    .padding(top = CuteCardTokens.DismissButtonTopPadding)
                    .height(CuteCardTokens.DismissButtonReservedHeight)
            )
        }
    }
}

// ── Face routing helpers ───────────────────────────────────────────────────────

@Composable
private fun CardFrontFace(
    frontIsWordOnly: Boolean,
    content: CuteCardContent,
    style: CuteCardStyle,
    labels: CuteCardLabels,
    isPlaying: Boolean,
    onAudioRequested: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    if (frontIsWordOnly) {
        CardFront(
            content = content,
            style = style,
            labels = labels,
            modifier = modifier
        )
    } else {
        CardBack(
            content = content,
            style = style,
            labels = labels,
            isPlaying = isPlaying,
            onAudioRequested = onAudioRequested,
            modifier = modifier
        )
    }
}

@Composable
private fun CardBackFace(
    frontIsWordOnly: Boolean,
    content: CuteCardContent,
    style: CuteCardStyle,
    labels: CuteCardLabels,
    isPlaying: Boolean,
    onAudioRequested: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    if (frontIsWordOnly) {
        CardBack(
            content = content,
            style = style,
            labels = labels,
            isPlaying = isPlaying,
            onAudioRequested = onAudioRequested,
            modifier = modifier
        )
    } else {
        CardFront(
            content = content,
            style = style,
            labels = labels,
            modifier = modifier
        )
    }
}
