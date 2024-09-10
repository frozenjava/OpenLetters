package net.frozendevelopment.mailshare.feature.mail.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId
import net.frozendevelopment.mailshare.feature.mail.list.ui.EmptyListView
import net.frozendevelopment.mailshare.feature.mail.list.ui.LetterCell
import net.frozendevelopment.mailshare.feature.mail.list.ui.LetterList
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

@Composable
fun LetterListView(
    modifier: Modifier = Modifier,
    state: LetterListState,
    onNavDrawerClicked: () -> Unit,
    onScanClicked: () -> Unit,
    toggleCategory: (CategoryId?) -> Unit,
    setSearchTerms: (String) -> Unit,
    openLetter: (LetterId) -> Unit,
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
            modifier = Modifier.fillMaxSize(),
            state = state,
            onCellClicked = openLetter,
            onNavDrawerClicked = onNavDrawerClicked,
            onScanClicked = onScanClicked,
            selectCategory = toggleCategory,
            setSearchTerms = setSearchTerms,
        )
    }
}

@Composable
fun LetterListPreview(darkTheme: Boolean, state: LetterListState) {
    MailShareTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LetterListView(
                state = state,
                onNavDrawerClicked = {},
                onScanClicked = {},
                toggleCategory = {},
                setSearchTerms = {},
                openLetter = {},
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun LetterListEmptyPreviewLight() {
    LetterListPreview(darkTheme = false, state = LetterListState())
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun LetterListEmptyPreviewDark() {
    LetterListPreview(darkTheme = true, state = LetterListState())
}