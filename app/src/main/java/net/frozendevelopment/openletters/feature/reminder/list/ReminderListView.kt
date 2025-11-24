package net.frozendevelopment.openletters.feature.reminder.list

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.extensions.openAppSettings
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailDestination
import net.frozendevelopment.openletters.feature.reminder.form.ReminderFormDestination
import net.frozendevelopment.openletters.feature.reminder.list.ui.EmptyReminderListCell
import net.frozendevelopment.openletters.ui.components.ActionReminderCell
import net.frozendevelopment.openletters.ui.components.ReminderPeekMenu
import net.frozendevelopment.openletters.ui.navigation.LocalDrawerState
import net.frozendevelopment.openletters.ui.navigation.LocalNavigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.navigation3.navigation

@Serializable
object ReminderListDestination : NavKey

@OptIn(KoinExperimentalAPI::class)
fun Module.reminderListNavigation() =
    navigation<ReminderListDestination> { route ->
        val navigator = LocalNavigator.current
        val drawerState = LocalDrawerState.current
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
                        navigator.navigate(ReminderFormDestination(id))
                    } else {
                        navigator.navigate(ReminderDetailDestination(id))
                    }
                },
                createReminderClicked = { navigator.navigate(ReminderFormDestination()) },
                onDeleteReminderClicked = viewModel::delete,
            )
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListView(
    modifier: Modifier = Modifier,
    state: ReminderListState,
    openNavigationDrawer: () -> Unit,
    onReminderClicked: (id: ReminderId, edit: Boolean) -> Unit,
    onDeleteReminderClicked: (ReminderId) -> Unit,
    createReminderClicked: () -> Unit,
) {
    val context = LocalContext.current
    var showReminderPeek by remember { mutableStateOf<ReminderId?>(null) }

    showReminderPeek?.let {
        ReminderPeekMenu(
            reminderId = it,
            onDismissRequest = { showReminderPeek = null },
            onViewClick = { onReminderClicked(it, false) },
            onEditClick = { onReminderClicked(it, true) },
            onDeleteClick = { },
            onAcknowledgeClick = { },
            onLetterClicked = {},
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CenterAlignedTopAppBar(
            title = { Text(text = "Reminders") },
            navigationIcon = {
                IconButton(onClick = openNavigationDrawer) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Back",
                    )
                }
            },
            actions = {
                IconButton(onClick = { createReminderClicked() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Back",
                    )
                }
            },
        )

        if (!state.hasNotificationPermission) {
            Surface(
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                color = MaterialTheme.colorScheme.errorContainer,
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(R.string.notification_permission_required))
                    TextButton(onClick = { (context as? Activity)?.openAppSettings() }) {
                        Text(stringResource(R.string.grant))
                    }
                }
            }
        }

        if (state.isEmpty) {
            EmptyReminderListCell(
                modifier =
                    Modifier
                        .fillMaxWidth(.95f)
                        .padding(vertical = 16.dp),
                onClicked = createReminderClicked,
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 192.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.urgentReminders.isNotEmpty()) {
                item {
                    Text(
                        text = "Urgent",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                items(
                    items = state.urgentReminders,
                    key = { reminder -> reminder.value },
                ) { reminder ->
                    ActionReminderCell(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .animateItem(),
                        id = reminder,
                        onClick = { onReminderClicked(it, false) },
                        onLongClick = { showReminderPeek = it },
                        onDeleteClick = onDeleteReminderClicked,
                        onEditClick = { onReminderClicked(it, true) },
                    )
                }
            }

            if (state.upcomingReminders.isNotEmpty()) {
                item {
                    Text(
                        text = "Up and Coming",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                items(
                    items = state.upcomingReminders,
                    key = { reminder -> reminder.value },
                ) { reminder ->
                    ActionReminderCell(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .animateItem(),
                        id = reminder,
                        onClick = { onReminderClicked(it, false) },
                        onLongClick = { showReminderPeek = it },
                        onDeleteClick = onDeleteReminderClicked,
                        onEditClick = { onReminderClicked(it, true) },
                    )
                }
            }

            if (state.pastReminders.isNotEmpty()) {
                item {
                    Text(
                        text = "Past Reminders",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                items(
                    items = state.pastReminders,
                    key = { reminder -> reminder.value },
                ) { reminder ->
                    ActionReminderCell(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .animateItem(),
                        id = reminder,
                        onClick = { onReminderClicked(it, false) },
                        onLongClick = { showReminderPeek = it },
                        onDeleteClick = onDeleteReminderClicked,
                        onEditClick = { onReminderClicked(it, true) },
                    )
                }
            }
        }
    }
}
