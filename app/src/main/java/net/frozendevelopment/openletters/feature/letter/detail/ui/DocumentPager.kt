package net.frozendevelopment.openletters.feature.letter.detail.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.ui.components.BrokenImageView
import net.frozendevelopment.openletters.ui.components.LazyImageView

@Composable
fun DocumentPager(
    modifier: Modifier = Modifier,
    body: String?,
    documents: Map<DocumentId, Uri?>,
) {
    val pageIndexOffset: Int = if (!body.isNullOrBlank()) 1 else 0
    val pagerState = rememberPagerState {
        if (!body.isNullOrBlank()) documents.size + 1 else documents.size
    }

    Box {
        HorizontalPager(
            modifier = modifier,
            state = pagerState,
        ) { page ->
            if (page == 0 && !body.isNullOrBlank()) {
                TranscriptionText(
                    modifier = Modifier.fillMaxSize(),
                    body = body
                )
            } else {
                val documentUri = documents.values.toList()[page - pageIndexOffset]
                if (documentUri != null) {
                    LazyImageView(
                        modifier = Modifier.fillMaxSize(),
                        uri = documentUri
                    )
                } else {
                    BrokenImageView(modifier = Modifier.fillMaxSize())
                }
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
private fun TranscriptionText(
    modifier: Modifier = Modifier,
    body: String
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
