package net.frozendevelopment.openletters.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

val Color.contrastColor: Color
    get() = if (luminance() > 0.5) Color.Black else Color.White

val Color.Companion.Random: Color
    get() =
        Color(
            red = kotlin.random.Random.nextInt(0, 255),
            green = kotlin.random.Random.nextInt(0, 255),
            blue = kotlin.random.Random.nextInt(0, 255),
        )
