package net.frozendevelopment.openletters.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

class ListDetailScene<T: Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>,
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOf(listEntry, detailEntry)
    override val content: @Composable (() -> Unit) = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.4f)) {
                listEntry.Content()
            }
            Column(modifier = Modifier.weight(0.6f)) {
                AnimatedContent(
                    targetState = detailEntry,
                    contentKey = { entry -> entry.contentKey },
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { it }
                        ) togetherWith
                                slideOutHorizontally(targetOffsetX = { -it })
                    }
                ) { entry ->
                    entry.Content()
                }
            }
        }
    }

    companion object {
        internal const val LIST_KEY = "ListDetailScene-List"
        internal const val DETAIL_KEY = "ListDetailScene-Detail"

        fun listPane() = mapOf(LIST_KEY to true)  + NavDisplay.transitionSpec {
            EnterTransition.None togetherWith ExitTransition.None
        } + NavDisplay.popTransitionSpec {
            EnterTransition.None togetherWith ExitTransition.None
        } + NavDisplay.predictivePopTransitionSpec {
            EnterTransition.None togetherWith ExitTransition.None
        }
        fun detailPane() = mapOf(DETAIL_KEY to true) + NavDisplay.transitionSpec {
            EnterTransition.None togetherWith ExitTransition.None
        } + NavDisplay.popTransitionSpec {
            EnterTransition.None togetherWith ExitTransition.None
        } + NavDisplay.predictivePopTransitionSpec {
            EnterTransition.None togetherWith ExitTransition.None
        }
    }
}

@Composable
fun <T : Any> rememberListDetailSceneStrategy(): ListDetailSceneStrategy<T> {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    return remember(windowSizeClass) {
        ListDetailSceneStrategy(windowSizeClass)
    }
}

class ListDetailSceneStrategy<T: Any>(val windowSizeClass: WindowSizeClass) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        val detailEntry = entries.lastOrNull()?.takeIf { it.metadata.containsKey(ListDetailScene.DETAIL_KEY) } ?: return null
        val listEntry = entries.findLast { it.metadata.containsKey(ListDetailScene.LIST_KEY) } ?: return null

        val sceneKey = listEntry.contentKey

        return ListDetailScene(
            sceneKey,
            entries.dropLast(1),
            listEntry,
            detailEntry
        )
    }
}