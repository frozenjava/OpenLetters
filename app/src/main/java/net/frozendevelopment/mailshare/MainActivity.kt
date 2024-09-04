package net.frozendevelopment.mailshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import net.frozendevelopment.mailshare.feature.category.categories
import net.frozendevelopment.mailshare.feature.category.form.CATEGORY_FORM_ROUTE
import net.frozendevelopment.mailshare.feature.mail.list.LETTER_LIST_ROUTE
import net.frozendevelopment.mailshare.feature.mail.list.letters
import net.frozendevelopment.mailshare.feature.mail.scan.scan
import net.frozendevelopment.mailshare.ui.components.MailNavDrawer
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MailShareTheme {
                val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navHostController = rememberNavController()

                MailNavDrawer(
                    drawerState = drawerState,
                    goToMail = {},
                    goToThreads = {},
                    goToManageCategories = {},
                    goToCreateCategory = {
                        navHostController.navigate(CATEGORY_FORM_ROUTE)
                    },
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .consumeWindowInsets(innerPadding)
                            .windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Horizontal,
                                ),
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
                                categories(navHostController)
                            }
                        }
                    }
                }

            }
        }
    }
}
