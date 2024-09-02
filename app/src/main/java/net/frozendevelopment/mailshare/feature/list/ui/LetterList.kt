package net.frozendevelopment.mailshare.feature.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId

@Composable
fun LetterList(
    modifier: Modifier = Modifier,
    letters: List<LetterId>,
    onCellClicked: (LetterId) -> Unit,
    onScanClicked: () -> Unit,
) {
    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 128.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = letters,
                key = { it.value }
            ) {
                LetterCell(
                    modifier = Modifier.fillMaxWidth(),
                    id = it,
                    onClick = onCellClicked,
                )
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 28.dp, vertical = 64.dp),
            onClick = onScanClicked
        ) {
            Icon(imageVector = Icons.Outlined.DocumentScanner, contentDescription = "Import Mail")
        }
    }
}
