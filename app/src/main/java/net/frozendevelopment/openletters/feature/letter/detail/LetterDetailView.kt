package net.frozendevelopment.openletters.feature.letter.detail

import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.mock.mockCategory
import net.frozendevelopment.openletters.data.mock.mockLetter
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.extensions.dateString
import net.frozendevelopment.openletters.feature.letter.image.ImageDestination
import net.frozendevelopment.openletters.feature.letter.scan.ScanLetterDestination
import net.frozendevelopment.openletters.feature.reminder.form.ReminderFormDestination
import net.frozendevelopment.openletters.ui.components.BrokenImageView
import net.frozendevelopment.openletters.ui.components.CategoryPill
import net.frozendevelopment.openletters.ui.components.LazyImageView
import net.frozendevelopment.openletters.ui.navigation.LocalNavigator
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.navigation3.navigation

@Serializable
data class LetterDetailDestination(
    val letterId: LetterId,
) : NavKey

@OptIn(KoinExperimentalAPI::class)
fun Module.letterDetailNavigation() =
    navigation<LetterDetailDestination> { route ->
        val navigator = LocalNavigator.current
        val viewModel: LetterDetailViewModel = koinViewModel { parametersOf(route.letterId) }
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        Surface {
            LetterDetailView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onEditClicked = { navigator.navigate(ScanLetterDestination(route.letterId)) },
                onCreateReminderClicked = {
                    navigator.navigate(
                        ReminderFormDestination(preselectedLetters = listOf(route.letterId)),
                    )
                },
                onBackClicked = navigator::pop,
                onImageClick = { uri -> navigator.navigate(ImageDestination(uri.toString())) },
            )
        }
    }

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
            title = { Text(stringResource(R.string.letter)) },
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                    )
                }
            },
            actions = {
                if (state !is LetterDetailState.Detail) return@CenterAlignedTopAppBar

                var expanded by remember { mutableStateOf(false) }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.actions))
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
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start,
        contentPadding = PaddingValues(top = 16.dp, bottom = 128.dp),
    ) {
        if (state.categories.isNotEmpty()) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    contentPadding = PaddingValues(start = 16.dp, end = 64.dp),
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
        }

        item {
            Text(
                text = stringResource(R.string.transcription),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Text(
                text = state.letter.body ?: stringResource(R.string.no_transcript_available),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 64.dp),
            ) {
                items(state.documents.keys.toList()) {
                    val documentUri = state.documents[it]
                    if (documentUri != null) {
                        LazyImageView(
                            modifier =
                                Modifier
                                    .size(128.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = { onImageClick(documentUri) },
                                            onDoubleTap = { onImageClick(documentUri) },
                                        )
                                    },
                            uri = documentUri,
                        )
                    } else {
                        BrokenImageView(modifier = Modifier.size(128.dp))
                    }
                }
            }
        }

        item { HorizontalDivider() }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text =
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.from))
                            }

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                                append(state.letter.sender ?: stringResource(R.string.unknown))
                            }
                        },
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                )

                Text(
                    text =
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.to))
                            }

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                                append(state.letter.recipient ?: stringResource(R.string.unknown))
                            }
                        },
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                )

                Text(
                    text =
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.created))
                            }

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                                append(state.letter.created.dateString)
                            }
                        },
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                )

                Text(
                    text =
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.last_modified))
                            }

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                                append(state.letter.created.dateString)
                            }
                        },
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                )
            }
        }
    }
}

@Composable
private fun LetterNotFound() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.letter_not_found),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun Loading() {
    CircularProgressIndicator()
}

@Composable
private fun LetterDetailPreview(state: LetterDetailState) {
    OpenLettersTheme {
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
@PreviewLightDark
private fun LetterDetail() {
    LetterDetailPreview(
        state =
            LetterDetailState.Detail(
                letter = mockLetter(),
                documents =
                    mapOf(
                        DocumentId.random() to null,
                        DocumentId.random() to null,
                        DocumentId.random() to null,
                        DocumentId.random() to null,
                    ),
                categories =
                    listOf(CategoryId.random(), CategoryId.random(), CategoryId.random())
                        .mapIndexed { index, categoryId -> mockCategory(id = categoryId, label = "Category $index") },
                threads = emptyList(),
            ),
    )
}

@Composable
@PreviewLightDark
private fun LetterLoadingPreview() {
    LetterDetailPreview(
        state = LetterDetailState.Loading,
    )
}

@Composable
@PreviewLightDark
private fun LetterNotFoundPreview() {
    LetterDetailPreview(
        state = LetterDetailState.NotFound,
    )
}
