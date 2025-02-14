package net.frozendevelopment.openletters.feature.letter.detail.ui

import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.ui.components.BrokenImageView
import net.frozendevelopment.openletters.ui.components.LazyImageView
import net.frozendevelopment.openletters.ui.components.PagerIndicator

@Composable
fun DocumentPager(
    modifier: Modifier = Modifier,
    body: String?,
    documents: Map<DocumentId, Uri?>,
    onImageClick: (Uri) -> Unit,
) {
    val pageIndexOffset: Int = if (!body.isNullOrBlank()) 1 else 0
    val pagerState =
        rememberPagerState {
            if (!body.isNullOrBlank()) documents.size + 1 else documents.size
        }

    Box {
        HorizontalPager(
            modifier = modifier,
            state = pagerState,
        ) { page ->
            if (page == 0 && !body.isNullOrBlank()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    TranscriptionText(
                        modifier =
                            Modifier
                                .fillMaxWidth(.95f)
                                .align(Alignment.TopCenter),
                        body = body,
                    )
                }
            } else {
                val documentUri = documents.values.toList()[page - pageIndexOffset]
                if (documentUri != null) {
                    LazyImageView(
                        modifier =
                            Modifier.fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { onImageClick(documentUri) },
                                        onDoubleTap = { onImageClick(documentUri) },
                                    )
                                },
                        uri = documentUri,
                    )
                } else {
                    BrokenImageView(modifier = Modifier.fillMaxSize())
                }
            }
        }

        if (pagerState.pageCount > 1) {
            PagerIndicator(
                modifier = Modifier.align(Alignment.BottomCenter),
                currentPage = pagerState.currentPage,
                pageCount = pagerState.pageCount,
            )
        }
    }
}

@Composable
private fun TranscriptionText(
    modifier: Modifier = Modifier,
    body: String,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 128.dp),
    ) {
        item {
            Text(
                "Transcription",
                style = MaterialTheme.typography.titleLarge,
            )
        }

        item {
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }

        item {
            Text(
                body,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
