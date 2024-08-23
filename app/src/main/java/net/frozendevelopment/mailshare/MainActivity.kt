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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import net.frozendevelopment.mailshare.feature.list.LETTER_LIST_ROUTE
import net.frozendevelopment.mailshare.feature.list.letters
import net.frozendevelopment.mailshare.feature.scan.scan
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MailShareTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navHostController = rememberNavController()

                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
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
                            letters(navHostController)
                            scan(navHostController)
                        }
                    }
                }
            }
        }
    }
}
