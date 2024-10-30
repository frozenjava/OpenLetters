package net.frozendevelopment.openletters.feature.letter.scan.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.ui.components.LazyImageView

@Composable
fun DocumentRow(
    modifier: Modifier = Modifier,
    transcript: String?,
    isLoadingTranscript: Boolean,
    onEditTranscript: (String) -> Unit,
    documents: Map<DocumentId, Uri>,
    onAddButtonClicks: () -> Unit,
    onDeleteDocumentClicked: (DocumentId) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val initialPage = if (!transcript.isNullOrBlank() || isLoadingTranscript) 0 else 1
    val pagerState = rememberPagerState(initialPage) { documents.size + 2 }

    Box {
        HorizontalPager(
            modifier = modifier,
            state = pagerState,
        ) { page ->
            if (page == 0) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        "Transcription",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    HorizontalDivider()
                    if (!isLoadingTranscript) {
                        BasicTextField(
                            modifier = Modifier
                                .fillMaxSize()
                                .imePadding(),
                            value = transcript?.plus("\n\n\n") ?: "",
                            onValueChange = onEditTranscript,
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                        ) { innerTextField ->
                            if (transcript.isNullOrBlank()) {
                                Text("No transcription available.")
                            }
                            innerTextField()
                        }
                    } else {
                        CircularProgressIndicator()
                    }
                }
            } else if (page == pagerState.pageCount - 1) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    TextButton(onClick = onAddButtonClicks) {
                        Text("Add Documents")
                    }
                }
            } else {
                val documentId = documents.keys.elementAt(page - 1)
                val uri = documents.getValue(documentId)
                ImageView(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxSize(),
                    uri = uri,
                    onDeleteClicked = { onDeleteDocumentClicked(documentId) }
                )
            }
        }

        Row(
            Modifier
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 4.dp)
                .background(Color.Black.copy(alpha = 0.85f), shape = RoundedCornerShape(16.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.White else Color.Gray
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
private fun ImageView(
    modifier: Modifier = Modifier,
    uri: Uri,
    onDeleteClicked: () -> Unit
) {
    Box(modifier) {
        LazyImageView(
            modifier = Modifier.fillMaxSize(),
            uri = uri
        )
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onDeleteClicked,
        ) {
            Icon(
                imageVector = Icons.Default.RemoveCircleOutline,
                contentDescription = "Remove",
                tint = Color.Red,
            )
        }
    }
}
