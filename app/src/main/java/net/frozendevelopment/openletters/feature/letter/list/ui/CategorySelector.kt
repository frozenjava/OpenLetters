package net.frozendevelopment.openletters.feature.letter.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.ui.components.CategoryPill

@Composable
fun CategorySelector(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    selectedCategoryId: CategoryId?,
    categories: List<Category>,
    toggleCategory: (CategoryId?) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    LazyRow(
        modifier = modifier,
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(start = 16.dp, end = 64.dp)
    ) {
        item {
            CategoryPill(
                label = "All",
                color = Color.Black,
                isSelected = selectedCategoryId == null,
                onToggle = { toggleCategory(null) }
            )
        }

        items(
            items = categories,
            key = { it.id.value }
        ) { category ->
            CategoryPill(
                category = category,
                isSelected = category.id == selectedCategoryId,
                onToggle = {
                    toggleCategory(category.id)

                    coroutineScope.launch {
                        listState.animateScrollToItem(categories.indexOf(category))
                    }
                }
            )
        }
    }
}
