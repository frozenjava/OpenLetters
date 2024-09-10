package net.frozendevelopment.mailshare.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

val Color.contrastColor: Color
    get() = if (luminance() > 0.5) Color.Black else Color.White
