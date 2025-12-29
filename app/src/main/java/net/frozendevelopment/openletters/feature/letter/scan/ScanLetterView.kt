package net.frozendevelopment.openletters.feature.letter.scan

import android.app.Activity.RESULT_OK
import android.net.Uri
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.feature.category.form.CategoryFormDestination
import net.frozendevelopment.openletters.feature.letter.detail.LetterDetailDestination
import net.frozendevelopment.openletters.feature.letter.list.LetterListDestination
import net.frozendevelopment.openletters.feature.letter.scan.ui.CategoryPicker
import net.frozendevelopment.openletters.feature.letter.scan.ui.ScanAppBar
import net.frozendevelopment.openletters.feature.letter.scan.ui.ScannableTextField
import net.frozendevelopment.openletters.ui.components.BrokenImageView
import net.frozendevelopment.openletters.ui.components.LazyImageView
import net.frozendevelopment.openletters.ui.navigation.LocalNavigator
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.navigation3.navigation
import java.time.LocalDateTime

@Serializable
data class ScanLetterDestination(
    val letterId: LetterId? = null,
    val canNavigateBack: Boolean = true,
) : NavKey

@OptIn(KoinExperimentalAPI::class)
fun Module.scanLetterNavigation() = navigation<ScanLetterDestination> { route ->
    val navigator = LocalNavigator.current
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalActivity.current
    val viewModel: ScanViewModel = koinViewModel { parametersOf(route.letterId) }
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    var openedScannerOnInitialization: Boolean = rememberSaveable { false }

    val letterScanLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                viewModel.importScannedDocuments(scanResult)
            }
        }

    val senderScanLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                viewModel.importScannedSender(scanResult)
            }
        }

    val recipientScanLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                viewModel.importScannedRecipient(scanResult)
            }
        }

    // Open the scanner view if the letter is not being edited
    if (route.letterId == null && !openedScannerOnInitialization) {
        if (activity != null) {
            openedScannerOnInitialization = true
            viewModel
                .getScanner()
                .getStartScanIntent(activity)
                .addOnSuccessListener { intentSender ->
                    letterScanLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                }.addOnFailureListener {
                    Log.e("ScanNavigation", "Scanner failed to load")
                }
        }
    }

    Surface {
        ScanLetterView(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding(),
            state = state,
            canNavigateBack = route.canNavigateBack,
            toggleCategory = viewModel::toggleCategory,
            setSender = viewModel::setSender,
            setRecipient = viewModel::setRecipient,
            setTranscript = viewModel::setTranscript,
            openLetterScanner = {
                if (activity != null) {
                    viewModel
                        .getScanner()
                        .getStartScanIntent(activity)
                        .addOnSuccessListener { intentSender ->
                            letterScanLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                        }.addOnFailureListener {
                            Log.e("ScanNavigation", "Scanner failed to load")
                        }
                }
            },
            openSenderScanner = {
                if (activity != null) {
                    viewModel
                        .getScanner(pageLimit = 1)
                        .getStartScanIntent(activity)
                        .addOnSuccessListener { intentSender ->
                            senderScanLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                        }.addOnFailureListener {
                            Log.e("ScanNavigation", "Scanner failed to load")
                        }
                }
            },
            openRecipientScanner = {
                if (activity != null) {
                    viewModel
                        .getScanner(pageLimit = 1)
                        .getStartScanIntent(activity)
                        .addOnSuccessListener { intentSender ->
                            recipientScanLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                        }.addOnFailureListener {
                            Log.e("ScanNavigation", "Scanner failed to load")
                        }
                }
            },
            onSaveClicked = {
                coroutineScope.launch(Dispatchers.IO) {
                    if (viewModel.save()) {
                        withContext(Dispatchers.Main) {
                            if (!route.canNavigateBack) {
                                // This happens if there is nothing on the backstack (fresh install)
                                // We need to add the list destination as the first item in the stack
                                // then add the detail and pop the form
                                navigator.navigate { backStack ->
                                    backStack.add(0, LetterListDestination)
                                    backStack.add(1, LetterDetailDestination(state.letterId))
                                    backStack.removeLastOrNull()
                                }
                            } else {
                                navigator.replace(route, LetterDetailDestination(state.letterId))
                            }
                        }
                    }
                }
            },
            onBackClicked = navigator::onBackPressed,
            onDeleteDocumentClicked = viewModel::removeDocument,
            onCreateCategoryClicked = { navigator.navigate(CategoryFormDestination(CategoryFormDestination.Mode.Create)) },
        )
    }
}

@Composable
fun ScanLetterView(
    modifier: Modifier = Modifier,
    state: ScanState,
    canNavigateBack: Boolean,
    setSender: (String) -> Unit,
    setRecipient: (String) -> Unit,
    toggleCategory: (Category) -> Unit,
    setTranscript: (String) -> Unit,
    openLetterScanner: () -> Unit,
    openSenderScanner: () -> Unit,
    openRecipientScanner: () -> Unit,
    onSaveClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onDeleteDocumentClicked: (DocumentId) -> Unit,
    onCreateCategoryClicked: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScanAppBar(
            canNavigateBack = canNavigateBack,
            state = state,
            onSaveClicked = onSaveClicked,
            onBackClicked = onBackClicked,
        )

        ScannableTextField(
            modifier = Modifier.fillMaxWidth(.95f),
            value = state.sender ?: "",
            label = "Sender",
            placeholder =
                """
                Jane Doe 123 Street Drive
                """.trimIndent(),
            suggestions = state.possibleSenders,
            onValueChange = setSender,
            onScanClick = openSenderScanner,
        )

        ScannableTextField(
            modifier = Modifier.fillMaxWidth(.95f),
            value = state.recipient ?: "",
            label = "Recipient",
            placeholder =
                """
                Jane Doe 123 Street Drive
                """.trimIndent(),
            suggestions = state.possibleRecipients,
            onValueChange = setRecipient,
            onScanClick = openRecipientScanner,
        )

        if (state.documents.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .fillMaxWidth(.95f)
                    .clickable(onClick = openLetterScanner),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(64.dp),
                    imageVector = Icons.Outlined.DocumentScanner,
                    contentDescription = "Scan",
                )
                Text(
                    text = "Scan Letter",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        } else {
            CategoryPicker(
                modifier = Modifier.fillMaxWidth(),
                categories = state.categoryMap,
                toggleCategory = toggleCategory,
                onCreateClicked = onCreateCategoryClicked,
            )
            TranscriptAndDocuments(
                modifier = Modifier.fillMaxSize(),
                state = state,
                openLetterScanner = openLetterScanner,
                onDeleteDocumentClicked = onDeleteDocumentClicked,
                onSaveTranscript = setTranscript,
                onImageClick = {
                    onSaveClicked()
                    onDeleteDocumentClicked(DocumentId.random())
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TranscriptAndDocuments(
    modifier: Modifier = Modifier,
    state: ScanState,
    openLetterScanner: () -> Unit,
    onDeleteDocumentClicked: (DocumentId) -> Unit,
    onSaveTranscript: (String) -> Unit,
    onImageClick: (Uri) -> Unit,
) {
    var showTranscriptEditor by remember { mutableStateOf(false) }
    val transcriptEditorBottomSheetState = rememberModalBottomSheetState(true)

    if (showTranscriptEditor) {
        ModalBottomSheet(
            onDismissRequest = { showTranscriptEditor = false },
            sheetState = transcriptEditorBottomSheetState,
            dragHandle = null,
        ) {
            val coroutineScope = rememberCoroutineScope()
            var transcript by remember { mutableStateOf(state.transcript ?: "") }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                transcriptEditorBottomSheetState.hide()
                                showTranscriptEditor = false
                            }
                        },
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }

                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                onSaveTranscript(transcript)
                                transcriptEditorBottomSheetState.hide()
                                showTranscriptEditor = false
                            }
                        },
                    ) {
                        Text(text = stringResource(R.string.save))
                    }
                }

                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = transcript,
                    onValueChange = { transcript = it },
                    minLines = 10,
                    maxLines = 30,
                )
            }
        }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 128.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier =
                        Modifier.weight(1f),
                    text = stringResource(R.string.transcription),
                    style = MaterialTheme.typography.titleLarge,
                )

                if (state.isCreatingTranscript) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    IconButton({ showTranscriptEditor = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Scan",
                        )
                    }
                }
            }

            if (state.isCreatingTranscript) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    repeat(5) { index ->
                        Box(
                            Modifier
                                .padding(horizontal = 16.dp)
                                .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                                .height(24.dp)
                                .fillMaxWidth(1f - (index * .1f)),
                        )
                    }
                }
            } else {
                SelectionContainer {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = state.transcript ?: stringResource(R.string.no_transcript_available),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 64.dp),
            ) {
                items(
                    items = state.documents.keys.toList(),
                    key = { it.value },
                ) {
                    val documentUri = state.documents[it]
                    if (documentUri != null) {
                        Box {
                            LazyImageView(
                                modifier = Modifier
                                    .size(128.dp)
                                    .clickable(onClick = { onImageClick(documentUri) }),
                                uri = documentUri,
                            )
                            IconButton(
                                modifier = Modifier.align(Alignment.TopEnd),
                                onClick = { onDeleteDocumentClicked(it) },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RemoveCircleOutline,
                                    contentDescription = "Remove",
                                    tint = Color.Red,
                                )
                            }
                        }
                    } else {
                        Box {
                            BrokenImageView(modifier = Modifier.size(128.dp))
                            IconButton(
                                modifier = Modifier.align(Alignment.TopEnd),
                                onClick = { onDeleteDocumentClicked(it) },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RemoveCircleOutline,
                                    contentDescription = "Remove",
                                    tint = Color.Red,
                                )
                            }
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .size(128.dp)
                            .clickable(onClick = openLetterScanner),
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                modifier = Modifier.size(64.dp),
                                imageVector = Icons.Outlined.DocumentScanner,
                                contentDescription = "Scan",
                            )

                            Text(text = "Add Document")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanFormPreview(state: ScanState) {
    OpenLettersTheme {
        Surface {
            ScanLetterView(
                canNavigateBack = false,
                state = state,
                toggleCategory = {},
                setSender = {},
                setRecipient = {},
                setTranscript = {},
                openLetterScanner = {},
                openSenderScanner = {},
                openRecipientScanner = {},
                onSaveClicked = {},
                onBackClicked = {},
                onDeleteDocumentClicked = {},
                onCreateCategoryClicked = {},
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun EmptyScanForm() {
    ScanFormPreview(ScanState())
}

@Composable
@PreviewLightDark
private fun FilledOutScanForm() {
    ScanFormPreview(
        ScanState(
            sender = "<NAME>",
            recipient = "<NAME>",
            transcript =
                """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                """.trimIndent(),
            newDocuments =
                mapOf(
                    DocumentId.random() to Uri.EMPTY,
                    DocumentId.random() to Uri.EMPTY,
                ),
            existingDocuments =
                mapOf(
                    DocumentId.random() to Uri.EMPTY,
                ),
            categories =
                listOf(
                    Category(
                        id = CategoryId.random(),
                        label = "Category 1",
                        color = Color.Red,
                        priority = 0,
                        created = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                    ),
                    Category(
                        id = CategoryId.random(),
                        label = "Category 2",
                        color = Color.Blue,
                        priority = 1,
                        created = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                    ),
                ),
            selectedCategories =
                setOf(
                    Category(
                        id = CategoryId.random(),
                        label = "Category 1",
                        color = Color.Red,
                        priority = 0,
                        created = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                    ),
                ),
        ),
    )
}

@Composable
@PreviewLightDark
private fun LoadingTranscript() {
    ScanFormPreview(
        ScanState(
            isCreatingTranscript = true,
            newDocuments =
                mapOf(
                    DocumentId.random() to Uri.EMPTY,
                ),
            categories =
                listOf(
                    Category(
                        id = CategoryId.random(),
                        label = "Category 1",
                        color = Color.Red,
                        priority = 0,
                        created = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                    ),
                    Category(
                        id = CategoryId.random(),
                        label = "Category 2",
                        color = Color.Blue,
                        priority = 1,
                        created = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                    ),
                ),
        ),
    )
}
