package net.frozendevelopment.openletters.feature.letter.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.feature.letter.list.LetterListState
import net.frozendevelopment.openletters.ui.components.LetterCell

@Composable
fun LetterList(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    state: LetterListState,
    onNavDrawerClicked: () -> Unit,
    onCellClicked: (LetterId) -> Unit,
    onScanClicked: () -> Unit,
    selectCategory: (CategoryId?) -> Unit,
    setSearchTerms: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            state = listState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 192.dp, top = 128.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = state.letters,
                key = { it.value }
            ) {
                LetterCell(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    id = it,
                    onClick = onCellClicked,
                )
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
                selectCategory(it)
                coroutineScope.launch {
                    listState.scrollToItem(0)
                }
            },
            onSearchChanged = {
                setSearchTerms(it)
                coroutineScope.launch {
                    listState.scrollToItem(0)
                }
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
