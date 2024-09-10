package net.frozendevelopment.mailshare.feature.mail.detail

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.data.mock.mockCategory
import net.frozendevelopment.mailshare.data.mock.mockLetter
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import net.frozendevelopment.mailshare.feature.mail.detail.ui.DocumentPager
import net.frozendevelopment.mailshare.ui.components.CategoryPill
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterDetailView(
    modifier: Modifier = Modifier,
    state: LetterDetailState,
    onBackClicked: () -> Unit,
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
        )

        when(state) {
            is LetterDetailState.Detail -> LetterDetail(
                modifier = Modifier.padding(horizontal = 16.dp),
                state = state
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
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = buildAnnotatedString {
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
                text = buildAnnotatedString {
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
                contentPadding = PaddingValues(start = 16.dp, end = 64.dp)
            ) {
                items(
                    items = state.categories,
                    key = { it.id.value },
                ) { category ->
                    CategoryPill(
                        category = category,
                        isSelected = true,
                        onToggle = {},
                    )
                }
            }
        }

        DocumentPager(
            modifier = Modifier.fillMaxWidth(),
            body = state.letter.body,
            documents = state.documents,
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
private fun LetterDetailPreview(darkTheme: Boolean, state: LetterDetailState) {
    MailShareTheme(darkTheme) {
        Surface {
            LetterDetailView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onBackClicked = {},
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun LetterDetailLight() {
    LetterDetailPreview(
        darkTheme = false,
        state = LetterDetailState.Detail(
            letter = mockLetter(),
            documents = emptyMap(),
            categories = listOf(CategoryId.random(), CategoryId.random(), CategoryId.random())
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