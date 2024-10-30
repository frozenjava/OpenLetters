package net.frozendevelopment.openletters.ui.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavBackStackEntry

private const val DURATION = 100

fun AnimatedContentTransitionScope<NavBackStackEntry>.navigationEnterTransition(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(DURATION)
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.navigationExitTransition(): ExitTransition {
    return scaleOut(
        targetScale = .95f,
        animationSpec = tween(DURATION)
    ) + fadeOut(targetAlpha = .5f, animationSpec = tween(DURATION))
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.navigationPopEnterTransition(): EnterTransition {
    return scaleIn(
        initialScale = .95f,
        animationSpec = tween(DURATION)
    ) + fadeIn(initialAlpha = .5f, animationSpec = tween(DURATION))
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.navigationPopExitTransition(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(DURATION)
    ) + scaleOut(targetScale = 1.05f, animationSpec = tween(DURATION))
}
