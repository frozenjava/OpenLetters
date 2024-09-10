package net.frozendevelopment.mailshare.feature.category.manage

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.zIndex
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import net.frozendevelopment.mailshare.feature.category.manage.ui.CategoryRow

const val MANAGE_CATEGORY_ROUTE = "/categories"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ManageCategoryView(
    state: ManageCategoryState,
    openNavigationDrawer: () -> Unit,
    editCategoryClicked: (CategoryId?) -> Unit,
    onDeleteClicked: (CategoryId) -> Unit,
    onMove: (Int, Int) -> Unit,
) {
    val listState = rememberLazyListState()
    var originalDraggedIndex by remember { mutableStateOf<Int?>(null) }
    var draggedItem by remember { mutableStateOf<CategoryId?>(null) }
    var draggedOffset by remember { mutableFloatStateOf(0f) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CenterAlignedTopAppBar(
            title = { Text(text = "Categories") },
            navigationIcon = {
                IconButton(onClick = openNavigationDrawer) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Back",
                    )
                }
            },
            actions = {
                IconButton(onClick = { editCategoryClicked(null) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Back",
                    )
                }
            }
        )

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 128.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxSize()

        ) {
            item {
                Text(
                    text = "Hold and drag to reorder",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            itemsIndexed(
                items = state.categories,
                key = { _, category -> category.id.value }
            ) { index, category ->
                CategoryRow(
                    category = category,
                    onEditClicked = editCategoryClicked,
                    onDeleteClicked = onDeleteClicked,
                    modifier = Modifier
                        .then(if (draggedItem == category.id) Modifier.offset { IntOffset(0, draggedOffset.toInt()) }.zIndex(1f) else Modifier)
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { offset ->
//                                    val startIndex = listState.layoutInfo.visibleItemsInfo
//                                        .firstOrNull { offset.y.toInt() in it.offset..it.offset + it.size }
//                                        ?.index
                                    val startIndex = index
                                    if (startIndex != null) {
                                        originalDraggedIndex = startIndex
                                        draggedItem = category.id
                                        draggedOffset =
                                            offset.y //- listState.layoutInfo.visibleItemsInfo[startIndex].offset
                                    }
                                },
                                onDragEnd = {
//                                    draggedItem?.let { d ->
//                                        onMove(d, listState.layoutInfo.visibleItemsInfo
//                                            .firstOrNull { draggedOffset.toInt() in it.offset..it.offset + it.size }
//                                            ?.index ?: d)
//                                    }
                                    draggedItem = null
                                    originalDraggedIndex = null
                                },
                                onDragCancel = { draggedItem = null },
                                onDrag = { change, dragAmount ->
                                    if (draggedItem != null) {
                                        draggedOffset += dragAmount.y
                                    }

                                    draggedItem?.let { d ->
                                        val adjustedOffset = (listState.layoutInfo.visibleItemsInfo[index].offset + draggedOffset).toInt()
                                        onMove(index, listState.layoutInfo.visibleItemsInfo
                                            .firstOrNull { adjustedOffset in it.offset..it.offset + it.size }
                                            ?.index ?: index)
                                    }
                                }
                            )
                        }
                )
            }
        }
    }
}
