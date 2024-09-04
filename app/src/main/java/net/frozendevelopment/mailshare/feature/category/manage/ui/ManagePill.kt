package net.frozendevelopment.mailshare.feature.category.manage.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import net.frozendevelopment.mailshare.ui.components.CategoryPill
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

@Composable
fun ManagePill(
    category: Category,
    onEditClicked: (CategoryId) -> Unit,
    onDeleteClicked: (CategoryId) -> Unit,
) {
    var height by remember { mutableStateOf(0.dp) }

    Row(
        modifier = Modifier
            .background(color = Color.Cyan)
            .onPlaced {
                height =it.size.height.dp
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onDeleteClicked(category.id) }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
        }

        CategoryPill(
            modifier = Modifier
                .height(height),
            color = Color(category.color)
        ) {
            Text(category.label)
        }

        IconButton(onClick = { onEditClicked(category.id) }) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
        }
    }
}

@Composable
@Preview
private fun ManagePillPreview() {
    MailShareTheme {
        Surface {
            ManagePill(
                category = Category(
                    id = CategoryId.random(),
                    label = "Some Label",
                    color = 0xFF0F0FF0,
                    created = 0,
                    lastModified = 0,
                ),
                onEditClicked = {},
                onDeleteClicked = {},
            )
        }
    }
}