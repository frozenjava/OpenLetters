package net.frozendevelopment.openletters.feature.letter.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.feature.letter.detail.LetterDetailDestination
import net.frozendevelopment.openletters.feature.letter.scan.openScanForm
import org.koin.androidx.compose.koinViewModel

const val LETTER_LIST_ROUTE  = "/letters"

fun NavGraphBuilder.letters(
    navController: NavController,
    drawerState: DrawerState,
) {
    composable(LETTER_LIST_ROUTE) {
        val coroutineScope = rememberCoroutineScope()
        val viewModel: LetterListViewModel = koinViewModel()
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(viewModel) {
            viewModel.load(
                categoryFilter = state.selectedCategoryId,
                searchTerms = state.searchTerms,
            )
        }

        Surface {
            LetterListView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onNavDrawerClicked = {
                    coroutineScope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                },
                onScanClicked = navController::openScanForm,
                toggleCategory = viewModel::toggleCategory,
                setSearchTerms = viewModel::setSearchTerms,
                openLetter = { navController.navigate(LetterDetailDestination(it)) },
            )
        }
    }
}
