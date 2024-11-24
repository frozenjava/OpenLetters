package net.frozendevelopment.openletters.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

@Composable
fun PulseIndicator(
    modifier: Modifier = Modifier,
    targetValue: Float = 1f,
    initialValue: Float = 0.98f,
    content: @Composable () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseInfiniteTransition")
    val scale by infiniteTransition.animateFloat(
        label = "pulseAnimation",
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 600,
                        easing = LinearOutSlowInEasing,
                    ),
                repeatMode = RepeatMode.Reverse,
            ),
    )

    Box(
        modifier = modifier.scale(scale),
    ) {
        content()
    }
}
