package net.frozendevelopment.openletters.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeCell(
    leftMenu: (@Composable (Modifier) -> Unit)? = null,
    rightMenu: (@Composable (Modifier) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val localDensity = LocalDensity.current
    var cellHeight by remember { mutableStateOf(0.dp) }
    var cellWidth by remember { mutableStateOf(0.dp) }
    var maxOffsetLeft = 0.dp
    var maxOffsetRight = 0.dp
    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { coroutineScope.launch { offsetX.stop() } },
                        onDragEnd = {
                            val currentOffset = offsetX.value
                            val threshold = 0.25f * maxOffsetLeft.toPx()
                            val thresholdRight = -0.25f * maxOffsetRight.toPx()
                            coroutineScope.launch {
                                if (currentOffset > threshold) {
                                    offsetX.animateTo(maxOffsetLeft.toPx())
                                } else if (currentOffset < thresholdRight) {
                                    offsetX.animateTo(-maxOffsetRight.toPx())
                                } else {
                                    offsetX.animateTo(0f)
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            if (offsetX.value == 0f && change.pressure < 0.4f) {
                                return@detectHorizontalDragGestures
                            }

                            coroutineScope.launch {
                                val newOffset = (offsetX.value + dragAmount).coerceIn(-maxOffsetRight.toPx(), maxOffsetLeft.toPx())
                                offsetX.snapTo(newOffset)
                            }
                        },
                    )
                },
    ) {
        // the background layer (hidden menu revealed by the swipe)
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier =
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .align(Alignment.CenterStart)
                    .width(cellWidth)
                    .height(cellHeight),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier =
                    Modifier
                        .align(Alignment.Center),
            ) {
                if (leftMenu != null) {
                    leftMenu(
                        Modifier
                            .padding(horizontal = 10.dp)
                            .height(cellHeight)
                            .onGloballyPositioned { coordinates ->
                                maxOffsetLeft = with(localDensity) { coordinates.size.width.toDp() + 10.dp }
                            },
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (rightMenu != null) {
                    rightMenu(
                        Modifier
                            .padding(horizontal = 10.dp)
                            .height(cellHeight)
                            .onGloballyPositioned { coordinates ->
                                maxOffsetRight = with(localDensity) { coordinates.size.width.toDp() + 10.dp }
                            },
                    )
                }
            }
        }

        // foreground cell layer
        Box(
            modifier =
                Modifier
                    .offset { IntOffset((offsetX.value).roundToInt(), 0) }
                    .shadow(
                        elevation = if (offsetX.value != 0f) 1.dp else 0.dp,
                        shape = MaterialTheme.shapes.medium,
                    )
                    .onGloballyPositioned { coordinates ->
                        cellHeight = with(localDensity) { coordinates.size.height.toDp() }
                        cellWidth = with(localDensity) { coordinates.size.width.toDp() }
                    },
        ) {
            content()
        }
    }
}
