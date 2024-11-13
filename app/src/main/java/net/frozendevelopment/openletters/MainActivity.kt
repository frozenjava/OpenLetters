package net.frozendevelopment.openletters

import android.content.pm.ActivityInfo
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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.extensions.newRoot
import net.frozendevelopment.openletters.feature.category.categories
import net.frozendevelopment.openletters.feature.category.form.CategoryFormDestination
import net.frozendevelopment.openletters.feature.category.manage.ManageCategoryDestination
import net.frozendevelopment.openletters.feature.letter.letters
import net.frozendevelopment.openletters.feature.letter.list.LetterListDestination
import net.frozendevelopment.openletters.feature.reminder.form.ReminderFormDestination
import net.frozendevelopment.openletters.feature.reminder.list.ReminderListDestination
import net.frozendevelopment.openletters.feature.reminder.reminders
import net.frozendevelopment.openletters.feature.settings.SettingsDestination
import net.frozendevelopment.openletters.feature.settings.settings
import net.frozendevelopment.openletters.ui.animation.navigationEnterTransition
import net.frozendevelopment.openletters.ui.animation.navigationExitTransition
import net.frozendevelopment.openletters.ui.animation.navigationPopEnterTransition
import net.frozendevelopment.openletters.ui.animation.navigationPopExitTransition
import net.frozendevelopment.openletters.ui.components.MailNavDrawer
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme
import net.frozendevelopment.openletters.util.ThemeManagerType
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val themeManager: ThemeManagerType by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val currentTheme by themeManager.current.collectAsStateWithLifecycle()

            OpenLettersTheme(
                appTheme = currentTheme.first,
                colorPalette = currentTheme.second,
            ) {
                val coroutineScope = rememberCoroutineScope()
                val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navHostController = rememberNavController()

                // lock the app to portrait for phone users
                if (currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass ==  WindowWidthSizeClass.COMPACT) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }

                MailNavDrawer(
                    drawerState = drawerState,
                    goToMail = {
                        coroutineScope.launch { drawerState.close() }
                        navHostController.newRoot(LetterListDestination)
                    },
                    goToManageCategories = {
                        coroutineScope.launch { drawerState.close() }
                        navHostController.newRoot(ManageCategoryDestination)
                    },
                    goToCreateCategory = {
                        coroutineScope.launch { drawerState.close() }
                        navHostController.navigate(CategoryFormDestination())
                    },
                    goToReminders = {
                        coroutineScope.launch { drawerState.close() }
                        navHostController.newRoot(ReminderListDestination)
                    },
                    goToCreateReminder = {
                        coroutineScope.launch { drawerState.close() }
                        navHostController.navigate(ReminderFormDestination())
                    },
                    goToSettings = {
                        coroutineScope.launch { drawerState.close() }
                        navHostController.navigate(SettingsDestination)
                    },
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Horizontal
                                )
                            )
                        ) {
                            NavHost(
                                navController = navHostController,
                                startDestination = LetterListDestination,
                                enterTransition = { navigationEnterTransition() },
                                exitTransition = { navigationExitTransition() },
                                popEnterTransition = { navigationPopEnterTransition() },
                                popExitTransition = { navigationPopExitTransition() },
                            ) {
                                categories(navHostController, drawerState)
                                letters(navHostController, drawerState)
                                reminders(navHostController, drawerState)
                                settings(navHostController, drawerState)
                            }
                        }
                    }
                }
            }
        }
    }
}
