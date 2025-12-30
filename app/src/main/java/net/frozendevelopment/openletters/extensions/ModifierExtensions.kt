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
import androidx.compose.ui.draw.scale

@Composable
fun Modifier.pulse(
    initialScale: Float = 1f,
    targetScale: Float = 0.95f,
    durationMillis: Int = 600,
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseInfiniteTransition")
    val scale by infiniteTransition.animateFloat(
        label = "pulseAnimation",
        initialValue = initialScale,
        targetValue = targetScale,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = durationMillis,
                        easing = FastOutLinearInEasing,
                    ),
                repeatMode = RepeatMode.Reverse,
            ),
    )

    return this.scale(scale)
}
