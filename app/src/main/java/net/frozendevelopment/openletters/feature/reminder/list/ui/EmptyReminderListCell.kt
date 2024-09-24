package net.frozendevelopment.openletters.feature.reminder.list.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.ui.theme.tipCardColors

@Composable
fun EmptyReminderListCell(
    modifier: Modifier = Modifier,
    onClicked: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier,
        colors = tipCardColors,
        onClick = onClicked
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Alarm,
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Create Reminder",
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
            Text(
                text = "Set up reminders to receive notifications for important tasks, like bill due dates or calls you need to make. You can also attach specific letters your reminders, keeping all related information in one place and ensuring you stay organized and efficient.",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}
