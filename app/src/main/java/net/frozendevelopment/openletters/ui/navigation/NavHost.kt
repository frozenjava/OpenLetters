package net.frozendevelopment.openletters.ui.navigation

import android.media.CamcorderProfile.getAll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.LocalKoinScopeContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.scope.Scope

typealias EntryProvider = (NavKey) -> NavEntry<NavKey>

@OptIn(KoinExperimentalAPI::class, KoinInternalApi::class)
@KoinExperimentalAPI
@Composable
fun koinEntryProvider(scope: Scope = LocalKoinScopeContext.current.getValue()): EntryProvider {
    val entries: List<EntryProviderScope<NavKey>.() -> Unit> = scope.getAll()
    val entryProvider: (NavKey) -> NavEntry<NavKey> =
        entryProvider {
            entries.forEach { builder -> this.builder() }
        }
    return entryProvider
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun NavHost(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>,
    entryProvider: EntryProvider = koinEntryProvider(),
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navigationState = rememberNavigationState(startRoute, topLevelRoutes)
    val navigator = remember { Navigator(navigationState) }

    NavHost(
        drawerState = drawerState,
        navigationState = navigationState,
        navigator = navigator,
    ) {
        NavDisplay(
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.pop() },
        )
    }
}

@Composable
fun NavHost(
    drawerState: DrawerState,
    navigationState: NavigationState,
    navigator: NavigatorType,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalDrawerState provides drawerState) {
        CompositionLocalProvider(LocalNavigationState provides navigationState) {
            CompositionLocalProvider(
                LocalNavigator provides navigator,
            ) {
                content()
            }
        }
    }
}
