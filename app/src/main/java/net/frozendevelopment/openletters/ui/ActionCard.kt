package net.frozendevelopment.openletters.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    CompositionLocalProvider(
        LocalContentColor provides colors.contentColor
    ) {
        Box(
            modifier = modifier
                .minimumInteractiveComponentSize()
                .background(
                    color = colors.containerColor,
                    shape = MaterialTheme.shapes.medium
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onClick() },
                        onLongPress = onLongClick?.let {{
                            it()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }}
                    )
                }
        ) {
            content()
        }
    }
}
