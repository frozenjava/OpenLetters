package net.frozendevelopment.openletters.feature.letter.detail

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.mock.mockCategory
import net.frozendevelopment.openletters.data.mock.mockLetter
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.feature.letter.detail.ui.DocumentPager
import net.frozendevelopment.openletters.ui.components.CategoryPill
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterDetailView(
    modifier: Modifier = Modifier,
    state: LetterDetailState,
    onEditClicked: () -> Unit,
    onCreateReminderClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onImageClick: (Uri) -> Unit,
) {
    Column(modifier = modifier) {
        CenterAlignedTopAppBar(
            title = { Text("Letter") },
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            },
            actions = {
                if (state !is LetterDetailState.Detail) return@CenterAlignedTopAppBar

                var expanded by remember { mutableStateOf(false) }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                }

                DropdownMenu(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.edit)) },
                        onClick = onEditClicked,
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.create_reminder)) },
                        onClick = onCreateReminderClicked,
                    )
                }
            },
        )

        when (state) {
            is LetterDetailState.Detail ->
                LetterDetail(
                    modifier = Modifier.padding(),
                    state = state,
                    onImageClick = onImageClick,
                )
            LetterDetailState.Loading -> Loading()
            LetterDetailState.NotFound -> LetterNotFound()
        }
    }
}

@Composable
fun LetterDetail(
    modifier: Modifier = Modifier,
    state: LetterDetailState.Detail,
    onImageClick: (Uri) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth(.95f)
                    .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(.5f),
                text =
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("From: ")
                        }

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                            append(state.letter.sender ?: "Unknown")
                        }
                    },
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
            )
            Text(
                text =
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("To: ")
                        }

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                            append(state.letter.recipient ?: "Unknown")
                        }
                    },
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
            )
        }

        if (state.categories.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(start = 8.dp, end = 64.dp),
            ) {
                items(
                    items = state.categories,
                    key = { it.id.value },
                ) { category ->
                    CategoryPill(
                        category = category,
                        isSelected = true,
                    )
                }
            }
        }

        DocumentPager(
            modifier = Modifier.fillMaxWidth(),
            body = state.letter.body,
            documents = state.documents,
            onImageClick = onImageClick,
        )
    }
}

@Composable
private fun LetterNotFound() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Letter not found",
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun Loading() {
    CircularProgressIndicator()
}

@Composable
private fun LetterDetailPreview(
    darkTheme: Boolean,
    state: LetterDetailState,
) {
    OpenLettersTheme(darkTheme) {
        Surface {
            LetterDetailView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onEditClicked = {},
                onCreateReminderClicked = {},
                onBackClicked = {},
                onImageClick = {},
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun LetterDetailLight() {
    LetterDetailPreview(
        darkTheme = false,
        state =
            LetterDetailState.Detail(
                letter = mockLetter(),
                documents = emptyMap(),
                categories =
                    listOf(CategoryId.random(), CategoryId.random(), CategoryId.random())
                        .mapIndexed { index, categoryId -> mockCategory(id = categoryId, label = "Category $index") },
                threads = emptyList(),
            ),
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun LetterDetailDark() {
    LetterDetailPreview(
        darkTheme = true,
        state = LetterDetailState.Loading,
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun LetterLoadingLight() {
    LetterDetailPreview(
        darkTheme = false,
        state = LetterDetailState.Loading,
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun LetterLoadingDark() {
    LetterDetailPreview(
        darkTheme = true,
        state = LetterDetailState.Loading,
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun LetterNotFoundLight() {
    LetterDetailPreview(
        darkTheme = false,
        state = LetterDetailState.NotFound,
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun LetterNotFoundDark() {
    LetterDetailPreview(
        darkTheme = true,
        state = LetterDetailState.NotFound,
    )
}
