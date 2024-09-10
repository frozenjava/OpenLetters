package net.frozendevelopment.mailshare.feature.mail.scan.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.frozendevelopment.mailshare.ui.components.LazyImageView

@Composable
fun DocumentRow(
    modifier: Modifier = Modifier,
    documents: List<Uri>,
    onDeleteDocumentClicked: (Int) -> Unit
) {
    LazyRow(
        modifier = modifier,
    ) {
        itemsIndexed(items = documents) { index, document ->
            Box(Modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
                LazyImageView(uri = document)
                IconButton(onClick = { onDeleteDocumentClicked(index) }) {
                    Icon(
                        modifier = Modifier.align(Alignment.TopEnd),
                        imageVector = Icons.Default.RemoveCircleOutline,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}
