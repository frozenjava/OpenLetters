package net.frozendevelopment.openletters.feature.letter.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.feature.letter.list.LetterListState
import net.frozendevelopment.openletters.feature.letter.peek.LetterPeekMenu
import net.frozendevelopment.openletters.ui.components.ActionLetterCell
import net.frozendevelopment.openletters.usecase.MetaLetterUseCase
import org.koin.compose.koinInject

@Composable
fun LetterList(
    modifier: Modifier = Modifier,
    state: LetterListState,
    onNavDrawerClicked: () -> Unit,
    onLetterClicked: (id: LetterId, edit: Boolean) -> Unit,
    onDeleteLetterClicked: (LetterId) -> Unit,
    onScanClicked: () -> Unit,
    selectCategory: (CategoryId?) -> Unit,
    setSearchTerms: (String) -> Unit,
    onReminderClicked: (id: ReminderId, edit: Boolean) -> Unit,
    onCreateReminderClicked: (List<LetterId>) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    letterUseCase: MetaLetterUseCase = koinInject(),
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    var showLetterPeek by remember { mutableStateOf<LetterId?>(null) }

    showLetterPeek?.let {
        LetterPeekMenu(
            letterId = it,
            onViewClick = { onLetterClicked(it, false) },
            onEditClick = { onLetterClicked(it, true) },
            onDeleteClick = { onDeleteLetterClicked(it) },
            onAddReminderClicked = { onCreateReminderClicked(listOf(it)) },
            onDismissRequest = { showLetterPeek = null }
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (
                windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT
                && (state.urgentReminders.isNotEmpty() || state.upcomingReminders.isNotEmpty())
            ) {
                ReminderColumn(
                    modifier = Modifier.fillMaxWidth(.33f),
                    urgentReminders = state.urgentReminders,
                    upComingReminders = state.upcomingReminders,
                    onReminderClicked = onReminderClicked,
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                state = listState,
                contentPadding = PaddingValues(bottom = 192.dp, top = 128.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (
                    windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
                    && state.urgentReminders.isNotEmpty()
                ) {
                    reminderRow(
                        reminders = state.urgentReminders,
                        onReminderClicked = onReminderClicked
                    )

                    item {
                        HorizontalDivider()
                    }
                }

                items(
                    items = state.letters,
                    key = { it.value }
                ) {
                    ActionLetterCell(
                        modifier = Modifier
                            .fillMaxWidth(.95f)
                            .animateItem(),
                        id = it,
                        onClick = { onLetterClicked(it, false) },
                        onLongClick = { showLetterPeek = it },
                        onEditClick = { onLetterClicked(it, true) },
                        onDeleteClick = onDeleteLetterClicked,
                        letterUseCase = letterUseCase,
                    )
                }
            }
        }

        if (state.letters.isEmpty()) {
            BadFilters(
                modifier = Modifier.align(Alignment.Center),
                onClearClick = {
                    selectCategory(null)
                    setSearchTerms("")
                    focusManager.clearFocus()
                }
            )
        }

        FilterBar(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) { /* Consume all touch events */ },
            searchTerms = state.searchTerms,
            selectedCategoryId = state.selectedCategoryId,
            categories = state.categories,
            onToggleCategory = {
                coroutineScope.launch {
                    listState.scrollToItem(0)
                }

                selectCategory(it)
            },
            onSearchChanged = {
                coroutineScope.launch {
                    listState.scrollToItem(0)
                }

                setSearchTerms(it)
            },
            onNavDrawerClicked = onNavDrawerClicked
        )

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 28.dp, vertical = 64.dp),
            onClick = onScanClicked
        ) {
            Icon(imageVector = Icons.Outlined.DocumentScanner, contentDescription = "Import Mail")
        }
    }
}

@Composable
private fun BadFilters(
    modifier: Modifier = Modifier,
    onClearClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            text = """
                        There are no letters to display.
                        Please check your filters.
                    """.trimIndent()
        )

        TextButton(onClick = onClearClick) {
            Text(text = "Clear Filters")
        }
    }
}
