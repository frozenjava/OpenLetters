package net.frozendevelopment.openletters.feature.category.manage

import android.util.Log
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.zIndex
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.feature.category.manage.ui.CategoryRow
import net.frozendevelopment.openletters.feature.category.manage.ui.EmptyCategoryListCell

@Serializable
object ManageCategoryDestination

private data class DraggableItem(val index: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoryView(
    listState: LazyListState = rememberLazyListState(),
    state: ManageCategoryState,
    openNavigationDrawer: () -> Unit,
    editCategoryClicked: (CategoryId?) -> Unit,
    onDeleteClicked: (CategoryId) -> Unit,
    onMove: (Int, Int) -> Unit,
    onMoveComplete: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    var draggingItem: LazyListItemInfo? by remember { mutableStateOf(null) }
    var draggingItemIndex: Int? by remember { mutableStateOf(null) }
    var delta: Float by remember { mutableFloatStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
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

        if (state.isEmpty) {
            EmptyCategoryListCell(
                modifier = Modifier.fillMaxWidth(.95f),
                onClicked = { editCategoryClicked(null) }
            )
        }

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 128.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(listState) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { offset ->
                            listState.layoutInfo.visibleItemsInfo
                                .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
                                ?.also {
                                    (it.contentType as? DraggableItem)?.let { draggableItem ->
                                        draggingItem = it
                                        draggingItemIndex = draggableItem.index
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            delta += dragAmount.y

                            val currentDraggingItemIndex = draggingItemIndex ?: return@detectDragGesturesAfterLongPress
                            val currentDraggingItem = draggingItem ?: return@detectDragGesturesAfterLongPress

                            val startOffset = currentDraggingItem.offset + delta
                            val endOffset = currentDraggingItem.offset + currentDraggingItem.size + delta
                            val middleOffset = startOffset + (endOffset - startOffset) / 2

                            val targetItem = listState.layoutInfo.visibleItemsInfo.find { item ->
                                middleOffset.toInt() in item.offset..item.offset + item.size &&
                                        currentDraggingItem.index != item.index &&
                                        item.contentType is DraggableItem
                            }

                            if (targetItem != null) {
                                val targetIndex = (targetItem.contentType as DraggableItem).index
                                onMove(currentDraggingItemIndex, targetIndex)
                                draggingItemIndex = targetIndex
                                draggingItem = targetItem
                                delta += currentDraggingItem.offset - targetItem.offset
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            } else {
                                val startOffsetToTop =
                                    startOffset - listState.layoutInfo.viewportStartOffset
                                val endOffsetToBottom =
                                    endOffset - listState.layoutInfo.viewportEndOffset
                                val scroll =
                                    when {
                                        startOffsetToTop < 0 -> startOffsetToTop.coerceAtMost(0f)
                                        endOffsetToBottom > 0 -> endOffsetToBottom.coerceAtLeast(0f)
                                        else -> 0f
                                    }
                                val canScrollDown = currentDraggingItemIndex != state.categories.size - 1 && endOffsetToBottom > 0
                                val canScrollUp = currentDraggingItemIndex != 0 && startOffsetToTop < 0
                                if (scroll != 0f && (canScrollUp || canScrollDown)) {
                                    // this should be done over a channel but for some reason
                                    // it always fails to send to the channel and i am out of time.
                                    // will revisit later
                                    coroutineScope.launch {
                                        listState.animateScrollBy(scroll * 5)
                                    }
                                }
                            }
                        },
                        onDragEnd = {
                            draggingItem = null
                            draggingItemIndex = null
                            delta = 0f
                            onMoveComplete()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onDragCancel = {
                            draggingItem = null
                            draggingItemIndex = null
                            delta = 0f
                            onMoveComplete()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                }
        ) {
            if (!state.isEmpty) {
                item {
                    Text(
                        text = "Hold and drag to reorder",
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
            }

            itemsIndexed(
                items = state.categories,
                key = { _, category -> category.id.value },
                contentType = { index, _ -> DraggableItem(index) }
            ) { index, category ->
                CategoryRow(
                    category = category,
                    onEditClicked = editCategoryClicked,
                    onDeleteClicked = onDeleteClicked,
                    modifier = if (draggingItemIndex == index) {
                        Modifier
                            .scale(1.02f)
                            .zIndex(1f)
                            .graphicsLayer {
                                translationY = delta
                            }
                    } else {
                        Modifier.animateItem()
                    }
                )
            }

//            itemsIndexed(
//                items = state.categories,
//                key = { _, category -> category.id.value }
//            ) { index, category ->
//                CategoryRow(
//                    category = category,
//                    onEditClicked = editCategoryClicked,
//                    onDeleteClicked = onDeleteClicked,
//                    modifier = Modifier
//                        .then(if (draggedItem == category.id) Modifier.offset { IntOffset(0, draggedOffset.toInt()) }.zIndex(1f) else Modifier)
//                        .pointerInput(Unit) {
//                            detectDragGesturesAfterLongPress(
//                                onDragStart = { offset ->
////                                    val startIndex = listState.layoutInfo.visibleItemsInfo
////                                        .firstOrNull { offset.y.toInt() in it.offset..it.offset + it.size }
////                                        ?.index
//                                    val startIndex = index
//                                    if (startIndex != null) {
//                                        originalDraggedIndex = startIndex
//                                        draggedItem = category.id
//                                        draggedOffset =
//                                            offset.y //- listState.layoutInfo.visibleItemsInfo[startIndex].offset
//                                    }
//                                },
//                                onDragEnd = {
////                                    draggedItem?.let { d ->
////                                        onMove(d, listState.layoutInfo.visibleItemsInfo
////                                            .firstOrNull { draggedOffset.toInt() in it.offset..it.offset + it.size }
////                                            ?.index ?: d)
////                                    }
//                                    draggedItem = null
//                                    originalDraggedIndex = null
//                                },
//                                onDragCancel = { draggedItem = null },
//                                onDrag = { change, dragAmount ->
//                                    if (draggedItem != null) {
//                                        draggedOffset += dragAmount.y
//                                    }
//
//                                    draggedItem?.let { d ->
//                                        val adjustedOffset = (listState.layoutInfo.visibleItemsInfo[index].offset + draggedOffset).toInt()
//                                        onMove(index, listState.layoutInfo.visibleItemsInfo
//                                            .firstOrNull { adjustedOffset in it.offset..it.offset + it.size }
//                                            ?.index ?: index)
//                                    }
//                                }
//                            )
//                        }
//                )
//            }
        }
    }
}
