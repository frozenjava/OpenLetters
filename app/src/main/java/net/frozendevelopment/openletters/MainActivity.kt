package net.frozendevelopment.openletters

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.feature.category.categories
import net.frozendevelopment.openletters.feature.category.form.CategoryFormDestination
import net.frozendevelopment.openletters.feature.category.manage.MANAGE_CATEGORY_ROUTE
import net.frozendevelopment.openletters.feature.letter.detail.letterDetail
import net.frozendevelopment.openletters.feature.letter.list.LETTER_LIST_ROUTE
import net.frozendevelopment.openletters.feature.letter.list.letters
import net.frozendevelopment.openletters.feature.letter.scan.scan
import net.frozendevelopment.openletters.ui.components.MailNavDrawer
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenLettersTheme {
                val coroutineScope = rememberCoroutineScope()
                val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navHostController = rememberNavController()

                MailNavDrawer(
                    drawerState = drawerState,
                    goToMail = {
                        navHostController.navigate(LETTER_LIST_ROUTE) {
                            popUpTo(navHostController.graph.id) {
                                inclusive = true
                            }
                        }
                        coroutineScope.launch { drawerState.close() }
                    },
                    goToThreads = {},
                    goToManageCategories = {
                        navHostController.navigate(MANAGE_CATEGORY_ROUTE) {
                            popUpTo(navHostController.graph.id) {
                                inclusive = true
                            }
                        }
                        coroutineScope.launch { drawerState.close() }
                    },
                    goToCreateCategory = {
                        navHostController.navigate(CategoryFormDestination())
                        coroutineScope.launch { drawerState.close() }
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
                                startDestination = LETTER_LIST_ROUTE,
                                enterTransition = { EnterTransition.None },
                                exitTransition = { ExitTransition.None },
                            ) {
                                letters(navHostController, drawerState)
                                scan(navHostController)
                                letterDetail(navHostController)
                                categories(navHostController, drawerState)
                            }
                        }
                    }
                }

            }
        }
    }
}
