package net.frozendevelopment.openletters

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.extensions.EntryProvider
import net.frozendevelopment.openletters.extensions.koinEntryProvider
import net.frozendevelopment.openletters.feature.category.form.CategoryFormDestination
import net.frozendevelopment.openletters.feature.category.manage.ManageCategoryDestination
import net.frozendevelopment.openletters.feature.letter.list.LetterListDestination
import net.frozendevelopment.openletters.feature.reminder.form.ReminderFormDestination
import net.frozendevelopment.openletters.feature.reminder.list.ReminderListDestination
import net.frozendevelopment.openletters.feature.settings.SettingsDestination
import net.frozendevelopment.openletters.ui.animation.popTransitionSpec
import net.frozendevelopment.openletters.ui.animation.pushTransitionSpec
import net.frozendevelopment.openletters.ui.navigation.LettersNavDrawer
import net.frozendevelopment.openletters.ui.navigation.LocalDrawerState
import net.frozendevelopment.openletters.ui.navigation.LocalNavigationState
import net.frozendevelopment.openletters.ui.navigation.LocalNavigator
import net.frozendevelopment.openletters.ui.navigation.Navigator
import net.frozendevelopment.openletters.ui.navigation.rememberNavigationState
import net.frozendevelopment.openletters.ui.navigation.toEntries
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme
import net.frozendevelopment.openletters.util.ThemeManagerType
import org.koin.android.ext.android.inject
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {
    private val themeManager: ThemeManagerType by inject()

    @OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            App()
        }
    }

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Composable
    private fun App() {
        val currentTheme by themeManager.current.collectAsStateWithLifecycle()

        val coroutineScope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val navigationState = rememberNavigationState(
            LetterListDestination,
            setOf(
                LetterListDestination,
                ManageCategoryDestination,
                ReminderListDestination,
            ),
        )
        val navigator = remember { Navigator(navigationState, onBackPressedDispatcher) }
        val entryProvider: EntryProvider = koinEntryProvider()

        val windowAdaptiveInfo = currentWindowAdaptiveInfo()
        val directive = remember(windowAdaptiveInfo) {
            calculatePaneScaffoldDirective(windowAdaptiveInfo)
                .copy(horizontalPartitionSpacerSize = 0.dp, verticalPartitionSpacerSize = 0.dp)
        }

        val supportingPaneStrategy = rememberListDetailSceneStrategy<NavKey>(
            backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
            directive = directive,
        )

        OpenLettersTheme(
            appTheme = currentTheme.first,
            colorPalette = currentTheme.second,
        ) {
            LettersNavDrawer(
                drawerState = drawerState,
                goToMail = {
                    coroutineScope.launch { drawerState.close() }
                    navigator.navigate(LetterListDestination)
                },
                goToManageCategories = {
                    coroutineScope.launch { drawerState.close() }
                    navigator.navigate(ManageCategoryDestination)
                },
                goToCreateCategory = {
                    coroutineScope.launch { drawerState.close() }
                    navigator.navigate(CategoryFormDestination())
                },
                goToReminders = {
                    coroutineScope.launch { drawerState.close() }
                    navigator.navigate(ReminderListDestination)
                },
                goToCreateReminder = {
                    coroutineScope.launch { drawerState.close() }
                    navigator.navigate(ReminderFormDestination())
                },
                goToSettings = {
                    coroutineScope.launch { drawerState.close() }
                    navigator.navigate(SettingsDestination)
                },
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                                .windowInsetsPadding(
                                    WindowInsets.safeDrawing.only(
                                        WindowInsetsSides.Horizontal,
                                    ),
                                ),
                    ) {
                        CompositionLocalProvider(LocalDrawerState provides drawerState) {
                            CompositionLocalProvider(LocalNavigationState provides navigationState) {
                                CompositionLocalProvider(
                                    LocalNavigator provides navigator,
                                ) {
                                    NavDisplay(
                                        entries = navigationState.toEntries(entryProvider),
                                        sceneStrategy = supportingPaneStrategy,
                                        onBack = { navigator.pop() },
                                        transitionSpec = { pushTransitionSpec() },
                                        popTransitionSpec = { popTransitionSpec() },
                                        predictivePopTransitionSpec = { popTransitionSpec() },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
