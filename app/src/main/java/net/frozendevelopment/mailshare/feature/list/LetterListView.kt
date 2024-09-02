package net.frozendevelopment.mailshare.feature.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.feature.list.ui.EmptyListView
import net.frozendevelopment.mailshare.feature.list.ui.LetterCell
import net.frozendevelopment.mailshare.feature.list.ui.LetterList
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

@Composable
fun LetterListView(
    modifier: Modifier = Modifier,
    state: LetterListState,
    onScanClicked: () -> Unit,
) {
    if (state.letters.isEmpty()) {
        EmptyListView(modifier = modifier, onScanClicked = onScanClicked)
    } else {
        LetterList(
            modifier = Modifier.fillMaxSize(),
            letters = state.letters,
            onCellClicked = {},
            onScanClicked = onScanClicked
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
            LetterListView(state = state, onScanClicked = {})
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