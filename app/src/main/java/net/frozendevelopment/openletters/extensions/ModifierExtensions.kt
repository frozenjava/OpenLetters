package net.frozendevelopment.openletters.extensions

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun Modifier.pulse() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseInfiniteTransition")
    val scale by infiniteTransition.animateFloat(
        label = "pulseAnimation",
        initialValue = 1f,
        targetValue = 0.95f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 600,
                        easing = FastOutLinearInEasing,
                    ),
                repeatMode = RepeatMode.Reverse,
            ),
    )
}
