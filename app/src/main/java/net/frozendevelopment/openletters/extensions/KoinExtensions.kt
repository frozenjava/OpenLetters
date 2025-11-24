package net.frozendevelopment.openletters.extensions

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import org.koin.compose.LocalKoinScopeContext
import org.koin.compose.navigation3.EntryProviderInstaller
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.module._scopedInstanceFactory
import org.koin.core.module._singleInstanceFactory
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import org.koin.dsl.navigation3.navigation

/**
 * Declares a scoped navigation entry within a Koin scope DSL.
 *
 * This function registers a composable navigation destination that is scoped to a specific Koin scope,
 * allowing access to scoped dependencies within the composable. The route type [T] is used as both
 * the navigation destination identifier and a qualifier for the entry provider.
 *
 * Example usage:
 * ```kotlin
 * activityScope {
 *     viewModel { MyViewModel() }
 *     navigation<MyRoute> { route ->
 *         MyScreen(viewModel = koinViewModel())
 *     }
 * }
 * ```
 *
 * @param T The type representing the navigation route/destination
 * @param definition A composable function that receives the [Scope] and route instance [T] to render the destination
 * @return A [KoinDefinition] for the created [EntryProviderInstaller]
 *
 * @see Module.navigation for module-level navigation entries
 */
@KoinExperimentalAPI
@KoinDslMarker
@OptIn(KoinInternalApi::class)
inline fun <reified T : NavKey> ScopeDSL.navigation(
    metadata: Map<String, Any> = emptyMap(),
    noinline definition: @Composable Scope.(T) -> Unit,
): KoinDefinition<EntryProviderInstaller> {
    val def = _scopedInstanceFactory<EntryProviderInstaller>(named<T>(), {
        val scope = this {
            entry<T>(
                metadata = metadata,
                content = { t -> definition(scope, t) },
            )
        }
    }, scopeQualifier)
    module.indexPrimaryType(def)
    return KoinDefinition(module, def)
}

/**
 * Declares a singleton navigation entry within a Koin module.
 *
 * This function registers a composable navigation destination as a singleton in the Koin module,
 * allowing access to module-level dependencies within the composable. The route type [T] is used
 * as both the navigation destination identifier and a qualifier for the entry provider.
 *
 * Example usage:
 * ```kotlin
 * module {
 *     viewModel { MyViewModel() }
 *     navigation<HomeRoute> { route ->
 *         HomeScreen(myViewModel = koinViewModel())
 *     }
 * }
 * ```
 *
 * @param T The type representing the navigation route/destination
 * @param definition A composable function that receives the [Scope] and route instance [T] to render the destination
 * @return A [KoinDefinition] for the created [EntryProviderInstaller]
 *
 * @see ScopeDSL.navigation for scope-level navigation entries
 */
@KoinExperimentalAPI
@KoinDslMarker
@OptIn(KoinInternalApi::class)
inline fun <reified T : Any> Module.navigation(
    metadata: Map<String, Any> = emptyMap(),
    noinline definition: @Composable Scope.(T) -> Unit,
): KoinDefinition<EntryProviderInstaller> {
    val def = _singleInstanceFactory<EntryProviderInstaller>(named<T>(), {
        val scope = this {
            entry<T>(
                metadata = metadata,
                content = { t -> definition(scope, t) },
            )
        }
    })
    indexPrimaryType(def)
    return KoinDefinition(this, def)
}

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
