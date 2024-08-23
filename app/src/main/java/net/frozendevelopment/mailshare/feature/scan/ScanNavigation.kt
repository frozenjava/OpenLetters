package net.frozendevelopment.mailshare.feature.scan

import android.content.Intent
import android.media.audiofx.BassBoost
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel

const val SCAN_FORM_ROUTE = "/letters/import"
const val SCAN_ROUTE = "/letters/import/scan"

fun NavController.openScanner(options: NavOptions = NavOptions.Builder().build()) {
    navigate(SCAN_ROUTE, options)
}

fun NavController.openScanForm(options: NavOptions = NavOptions.Builder().build()) {
    navigate(SCAN_FORM_ROUTE, options)
}

fun NavGraphBuilder.scan(navController: NavController) {
    composable(SCAN_FORM_ROUTE) {
        Surface {
            val viewModel: ScanViewModel = koinViewModel()
            val state by viewModel.stateFlow.collectAsStateWithLifecycle()

            ScanFormView(
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                openScanner = navController::openScanner
            )
        }
    }
    composable(
        SCAN_ROUTE,
        enterTransition = {
            slideInVertically(animationSpec = tween(durationMillis = 200)) { fullHeight ->
                fullHeight / 3
            } + fadeIn(animationSpec = tween(durationMillis = 200))
        },
        exitTransition = {
            slideOutVertically(animationSpec = tween(durationMillis = 200)) { fullHeight ->
                fullHeight / 3
            } + fadeOut(animationSpec = tween(durationMillis = 200))
        },
    ) {
        val backStackEntry = remember(it) { navController.getBackStackEntry(SCAN_FORM_ROUTE) }
        val viewModel: ScanViewModel = koinViewModel(viewModelStoreOwner = backStackEntry)
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()
        val context = LocalContext.current

        Surface {
            ScanView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
                state = state,
                onCloseClicked = navController::navigateUp,
                openSettingsClicked = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                },
            )
        }
    }
}
