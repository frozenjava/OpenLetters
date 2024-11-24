package net.frozendevelopment.openletters.feature.letter.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.ui.components.PagerIndicator
import net.frozendevelopment.openletters.ui.components.ReminderCell

fun LazyListScope.reminderRow(
    reminders: List<ReminderId>,
    onReminderClicked: (id: ReminderId, edit: Boolean) -> Unit,
) {
    item {
        Text(
            modifier = Modifier.fillMaxWidth(.95f),
            text = "Urgent Reminders",
            style = MaterialTheme.typography.labelLarge,
        )
    }

    item {
        val pagerState: PagerState = remember { PagerState { reminders.size } }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                contentPadding = PaddingValues(horizontal = 16.dp),
                state = pagerState,
                key = { it },
            ) { page: Int ->
                ReminderCell(
                    modifier = Modifier.fillMaxWidth(),
                    id = reminders[page],
                    onClick = { onReminderClicked(it, false) },
                )
            }

            if (reminders.size > 1) {
                PagerIndicator(
                    currentPage = pagerState.currentPage,
                    pageCount = reminders.size,
                )
            }
        }
    }
}
