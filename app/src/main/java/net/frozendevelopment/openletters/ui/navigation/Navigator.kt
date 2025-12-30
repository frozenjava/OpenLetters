package net.frozendevelopment.openletters.ui.navigation

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

val LocalNavigator = compositionLocalOf<NavigatorType> { error("Navigator not provided") }

interface NavigatorType {
    fun navigate(block: (NavBackStack<NavKey>) -> Unit)

    fun navigate(route: NavKey)

    fun replace(
        target: NavKey,
        destination: NavKey,
    )

    fun pop()

    fun onBackPressed()

    fun openUrl(url: String)
}

@Stable
class Navigator(
    val state: NavigationState,
    val backPressedDispatcher: OnBackPressedDispatcher? = null,
    val openInBrowser: (url: String) -> Unit = {},
) : NavigatorType {
    override fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            if (route == state.topLevelRoute) {
                state.backStacks[route]!!.removeAll { it != route }
            } else {
                state.topLevelRoute = route
            }
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    override fun navigate(block: (NavBackStack<NavKey>) -> Unit) {
        block(state.backStacks[state.topLevelRoute] ?: error("No back stack for current route"))
    }

    override fun replace(
        target: NavKey,
        destination: NavKey,
    ) {
        val currentStack = state.backStacks[state.topLevelRoute] ?: error("No back stack for current route")
        val index = currentStack.indexOfFirst { it == target }
        currentStack[index] = destination
    }

    override fun pop() {
        val currentStack = state.backStacks[state.topLevelRoute] ?: error("No back stack for current route")
        val currentRoute = currentStack.last()

        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }

    override fun onBackPressed() {
        backPressedDispatcher?.onBackPressed()
    }

    override fun openUrl(url: String) = openInBrowser(url)
}

class PreviewNavigator : NavigatorType {
    override fun navigate(block: (NavBackStack<NavKey>) -> Unit) {}

    override fun navigate(route: NavKey) {}

    override fun replace(
        target: NavKey,
        destination: NavKey,
    ) {}

    override fun pop() {}

    override fun onBackPressed() {}

    override fun openUrl(url: String) {}
}
