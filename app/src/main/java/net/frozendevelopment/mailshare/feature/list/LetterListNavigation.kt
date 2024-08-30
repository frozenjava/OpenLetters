package net.frozendevelopment.mailshare.feature.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import net.frozendevelopment.mailshare.feature.scan.openScanForm
import org.koin.androidx.compose.koinViewModel

const val LETTER_LIST_ROUTE  = "/letters"

fun NavGraphBuilder.letters(navController: NavController) {
    composable(LETTER_LIST_ROUTE) {
        val viewModel: LetterListViewModel = koinViewModel()
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        Surface {
            LetterListView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onScanClicked = navController::openScanForm,
            )
        }
    }
}
