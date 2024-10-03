package net.frozendevelopment.openletters.feature.letter.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.ui.components.ReminderCell

fun LazyListScope.reminderRow(
    reminders: List<ReminderId>,
    onViewAllClicked: () -> Unit,
    onReminderClicked: (id: ReminderId, edit: Boolean) -> Unit,
) {
    item {
        Text(
            modifier = Modifier.fillMaxWidth(.95f),
            text = "Urgent Reminders",
            style = MaterialTheme.typography.labelLarge
        )
    }

    item {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(
                items = reminders,
                key = { it.value }
            ) {
                ReminderCell(
                    modifier = Modifier.fillMaxWidth(),
                    id = it,
                    onClick = { onReminderClicked(it, false) },
                )
            }

            item {
                FilledTonalButton(onClick = onViewAllClicked) {
                    Text("View All")
                }
            }
        }
    }
}