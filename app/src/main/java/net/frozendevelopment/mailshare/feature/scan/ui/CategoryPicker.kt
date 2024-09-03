package net.frozendevelopment.mailshare.feature.scan.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category
import net.frozendevelopment.mailshare.ui.components.CategoryPill

@Composable
fun CategoryPicker(
    modifier: Modifier = Modifier,
    categories: Map<Category, Boolean>,
    toggleCategory: (Category) -> Unit,
    onCreateClicked: () -> Unit,
) {
    Column {
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(start = 16.dp, end = 32.dp)
        ) {
            items(
                items = categories.keys.toList(),
                key = { it.id.value }
            ) { category ->
                CategoryPill(
                    category = category,
                    isSelected = categories[category] == true,
                    onToggle = { toggleCategory(category) }
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .clip(ButtonDefaults.shape)
                        .border(1.dp, MaterialTheme.colorScheme.tertiary)
                        .clickable { onCreateClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.padding(ButtonDefaults.ContentPadding),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Text(text = "Create Tag")
                    }
                }
            }
        }
    }
}
