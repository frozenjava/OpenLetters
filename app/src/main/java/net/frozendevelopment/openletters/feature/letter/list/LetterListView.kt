package net.frozendevelopment.openletters.feature.letter.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.feature.letter.list.ui.EmptyListView
import net.frozendevelopment.openletters.feature.letter.list.ui.LetterList
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme

@Serializable
data object LetterListDestination

@Composable
fun LetterListView(
    modifier: Modifier = Modifier,
    state: LetterListState,
    onNavDrawerClicked: () -> Unit,
    onScanClicked: () -> Unit,
    toggleCategory: (CategoryId?) -> Unit,
    setSearchTerms: (String) -> Unit,
    openLetter: (id: LetterId, edit: Boolean) -> Unit,
    onDeleteLetterClicked: (id: LetterId) -> Unit,
    onReminderClicked: (id: ReminderId, edit: Boolean) -> Unit,
    onCreateReminderClicked: (List<LetterId>) -> Unit,
) {
    if (state.isLoading) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (state.showEmptyListView) {
        EmptyListView(
            modifier = modifier,
            onScanClicked = onScanClicked,
        )
    } else {
        LetterList(
            modifier = modifier,
            state = state,
            onLetterClicked = openLetter,
            onDeleteLetterClicked = onDeleteLetterClicked,
            onNavDrawerClicked = onNavDrawerClicked,
            onScanClicked = onScanClicked,
            selectCategory = toggleCategory,
            setSearchTerms = setSearchTerms,
            onReminderClicked = onReminderClicked,
            onCreateReminderClicked = onCreateReminderClicked,
        )
    }
}

@Composable
private fun LetterListPreview(state: LetterListState) {
    OpenLettersTheme {
        Surface(
            modifier =
                Modifier
                    .fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            LetterListView(
                modifier = Modifier.padding(vertical = 8.dp),
                state = state,
                onNavDrawerClicked = {},
                onScanClicked = {},
                toggleCategory = {},
                setSearchTerms = {},
                openLetter = { _, _ -> },
                onDeleteLetterClicked = {},
                onReminderClicked = { _, _ -> },
                onCreateReminderClicked = {},
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun EmptyPreview() {
    LetterListPreview(
        state = LetterListState(showEmptyListView = true, isLoading = false),
    )
}

@Composable
@PreviewLightDark
private fun LoadingPreview() {
    LetterListPreview(
        state = LetterListState(isLoading = true),
    )
}
