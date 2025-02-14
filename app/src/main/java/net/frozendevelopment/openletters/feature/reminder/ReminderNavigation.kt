package net.frozendevelopment.openletters.feature.reminder

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailScreen
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailState
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

        val notificationPermissionResultLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = viewModel::handlePermissionResult,
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !state.hasNotificationPermission) {
            LaunchedEffect(Unit) {
                notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
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
                onReminderClicked = { id, edit ->
                    if (edit) {
                        navController.navigate(ReminderFormDestination(id))
                    } else {
                        navController.navigate(ReminderDetailDestination(id))
                    }
                },
                createReminderClicked = { navController.navigate(ReminderFormDestination()) },
                onDeleteReminderClicked = viewModel::delete,
            )
        }
    }
    composable<ReminderDetailDestination>(
        typeMap = ReminderDetailDestination.typeMap,
        deepLinks = ReminderDetailDestination.deepLinks,
    ) { backStackEntry ->
        val destination = backStackEntry.toRoute<ReminderDetailDestination>()
        val viewModel = koinViewModel<ReminderDetailViewModel> { parametersOf(destination.reminderId) }
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        val notificationPermissionResultLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = viewModel::handlePermissionResult,
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (state as? ReminderDetailState.Detail)?.hasNotificationPermission == false) {
            LaunchedEffect(Unit) {
                notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        Surface {
            ReminderDetailScreen(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                onBackClicked = navController::navigateUp,
                onAcknowledgeClicked = viewModel::acknowledge,
                onLetterClicked = { navController.navigate(LetterDetailDestination(it)) },
            )
        }
    }
    composable<ReminderFormDestination>(
        typeMap = ReminderFormDestination.typeMap,
    ) { backStackEntry ->
        val destination = backStackEntry.toRoute<ReminderFormDestination>()
        val coroutineScope = rememberCoroutineScope()
        val viewModel =
            koinViewModel<ReminderFormViewModel> {
                parametersOf(destination.reminderId, destination.preselectedLetters)
            }
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        val notificationPermissionResultLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = viewModel::handlePermissionResult,
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !state.hasNotificationPermission) {
            LaunchedEffect(Unit) {
                notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
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
