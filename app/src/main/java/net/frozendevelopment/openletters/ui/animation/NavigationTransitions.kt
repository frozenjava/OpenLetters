package net.frozendevelopment.openletters.ui.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

private const val DURATION = 100

fun AnimatedContentTransitionScope<Scene<NavKey>>.navigationEnterTransition(): ContentTransform =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(DURATION),
    ) togetherWith scaleOut(
        targetScale = .95f,
        animationSpec = tween(DURATION),
    ) + fadeOut(targetAlpha = .5f, animationSpec = tween(DURATION))

fun AnimatedContentTransitionScope<Scene<NavKey>>.navigationExitTransition(): ExitTransition =
    scaleOut(
        targetScale = .95f,
        animationSpec = tween(DURATION),
    ) + fadeOut(targetAlpha = .5f, animationSpec = tween(DURATION))

fun AnimatedContentTransitionScope<Scene<NavKey>>.navigationPopEnterTransition(): EnterTransition =
    scaleIn(
        initialScale = .95f,
        animationSpec = tween(DURATION),
    ) + fadeIn(initialAlpha = .5f, animationSpec = tween(DURATION))

fun AnimatedContentTransitionScope<Scene<NavKey>>.navigationPopExitTransition(): ExitTransition =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(DURATION),
    ) + scaleOut(targetScale = 1.05f, animationSpec = tween(DURATION))

fun AnimatedContentTransitionScope<Scene<NavKey>>.popTransition(): ContentTransform =
    slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(DURATION),
    ) togetherWith scaleOut(
        targetScale = .95f,
        animationSpec = tween(DURATION),
    ) + fadeOut(targetAlpha = .5f, animationSpec = tween(DURATION))
