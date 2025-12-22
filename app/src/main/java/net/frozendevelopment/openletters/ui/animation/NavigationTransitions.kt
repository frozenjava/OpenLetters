package net.frozendevelopment.openletters.ui.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

private const val DURATION = 250

val pushTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    val incoming = slideInHorizontally(
        animationSpec = tween(DURATION),
    ) { fullWidth -> fullWidth }
    val outgoing = scaleOut(
        targetScale = 0.95f,
        animationSpec = tween(DURATION),
    ) + fadeOut(
        targetAlpha = 0.5f,
        animationSpec = tween(DURATION),
    )

    ContentTransform(
        targetContentEnter = incoming,
        initialContentExit = outgoing,
        sizeTransform = null,
    )
}

val popTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    val outgoing = slideOutHorizontally(
        animationSpec = tween(DURATION),
    ) { fullWidth -> fullWidth } + scaleOut(
        targetScale = 1.05f,
        animationSpec = tween(DURATION),
    ) + fadeOut(
        targetAlpha = 0.5f,
        animationSpec = tween(DURATION),
    )

    val incoming = scaleIn(
        initialScale = 0.95f,
        animationSpec = tween(DURATION),
    ) + fadeIn(
        initialAlpha = 0.5f,
        animationSpec = tween(DURATION),
    )

    ContentTransform(
        targetContentEnter = incoming,
        initialContentExit = outgoing,
        sizeTransform = null,
    )
}
