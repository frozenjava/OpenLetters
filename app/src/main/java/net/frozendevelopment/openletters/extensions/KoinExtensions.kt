package net.frozendevelopment.openletters.extensions

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
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
