package net.frozendevelopment.openletters.feature.reminder.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.feature.reminder.list.ui.EmptyReminderListCell
import net.frozendevelopment.openletters.ui.components.ReminderCell
import net.frozendevelopment.openletters.ui.components.ReminderPeekMenu

@Serializable
object ReminderListDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListView(
    modifier: Modifier = Modifier,
    state: ReminderListState,
    openNavigationDrawer: () -> Unit,
    onReminderClicked: (id: ReminderId, edit: Boolean) -> Unit,
    createReminderClicked: () -> Unit,
) {
    var showReminderPeek by remember { mutableStateOf<ReminderId?>(null) }

    showReminderPeek?.let {
        ReminderPeekMenu(
            reminderId = it,
            onDismissRequest = { showReminderPeek = null },
            onViewClick = { onReminderClicked(it, false) },
            onEditClick = { onReminderClicked(it, true) },
            onDeleteClick = { },
            onAcknowledgeClick = { },
            onLetterClicked = {}
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
            }
        )

        if (state.isEmpty) {
            EmptyReminderListCell(
                modifier = Modifier.fillMaxWidth(.95f),
                onClicked = createReminderClicked
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
                    key = { reminder -> reminder.value }
                ) { reminder ->
                    ReminderCell(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(),
                        id = reminder,
                        onClick = { onReminderClicked(it, false) },
                        onLongClick = { showReminderPeek = it },
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
                    key = { reminder -> reminder.value }
                ) { reminder ->
                    ReminderCell(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(),
                        id = reminder,
                        onClick = { onReminderClicked(it, false) },
                        onLongClick = { showReminderPeek = it },
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
                    key = { reminder -> reminder.value }
                ) { reminder ->
                    ReminderCell(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(),
                        id = reminder,
                        onClick = { onReminderClicked(it, false) },
                        onLongClick = { showReminderPeek = it },
                    )
                }
            }

        }
    }
}
