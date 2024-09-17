package net.frozendevelopment.openletters.feature.reminder

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.feature.letter.detail.LetterDetailDestination
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailDestination
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailView
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailViewModel
import net.frozendevelopment.openletters.feature.reminder.form.ReminderFormDestination
import net.frozendevelopment.openletters.feature.reminder.form.ReminderFormView
import net.frozendevelopment.openletters.feature.reminder.form.ReminderFormViewModel
import net.frozendevelopment.openletters.feature.reminder.list.ReminderListDestination
import net.frozendevelopment.openletters.feature.reminder.list.ReminderListView
import net.frozendevelopment.openletters.feature.reminder.list.ReminderListViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.reminders(
    navController: NavController,
    drawerState: DrawerState,
) {
    composable<ReminderListDestination> {
        val coroutineScope = rememberCoroutineScope()
        val viewModel = koinViewModel<ReminderListViewModel>()
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(viewModel) {
            viewModel.load()
        }

        Surface {
            ReminderListView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                openNavigationDrawer = {
                    coroutineScope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                },
                onReminderClicked = { navController.navigate(ReminderDetailDestination(it)) },
                createReminderClicked = { navController.navigate(ReminderFormDestination) }
            )
        }
    }
    composable<ReminderDetailDestination>(
        typeMap = ReminderDetailDestination.typeMap,
        deepLinks = ReminderDetailDestination.deepLinks
    ) { backStackEntry ->
        val destination = backStackEntry.toRoute<ReminderDetailDestination>()
        val viewModel = koinViewModel<ReminderDetailViewModel> { parametersOf(destination.reminderId) }
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(viewModel) {
            viewModel.load()
        }

        Surface {
            ReminderDetailView(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                onBackClicked = navController::navigateUp,
                onAcknowledgeClicked = viewModel::acknowledge,
                onLetterClicked = { navController.navigate(LetterDetailDestination(it)) }
            )
        }
    }
    composable<ReminderFormDestination> {
        val coroutineScope = rememberCoroutineScope()
        val viewModel = koinViewModel<ReminderFormViewModel>()
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(viewModel) {
            viewModel.load()
        }

        Surface {
            ReminderFormView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onTitleChanged = viewModel::setTitle,
                onDescriptionChanged = viewModel::setDescription,
                onDateSelected = viewModel::selectDate,
                onTimeSelected = viewModel::selectTime,
                toggleLetterSelect = viewModel::toggleLetterSelect,
                onLetterClicked = { navController.navigate(LetterDetailDestination(it)) },
                openDialog = viewModel::openDialog,
                onBackClicked = navController::navigateUp,
                onSaveClicked = {
                    coroutineScope.launch {
                        if (viewModel.save()) {
                            navController.navigateUp()
                        }
                    }
                },
            )
        }
    }
}
