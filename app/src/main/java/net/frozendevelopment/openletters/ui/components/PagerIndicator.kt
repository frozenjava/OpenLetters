package net.frozendevelopment.openletters.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PagerIndicator(
    modifier: Modifier = Modifier,
    currentPage: Int,
    pageCount: Int,
) {
    Box(modifier = modifier) {
        Row(
            Modifier
                .wrapContentHeight()
                .navigationBarsPadding()
                .padding(bottom = 4.dp)
                .background(Color.Black.copy(alpha = 0.85f), shape = RoundedCornerShape(16.dp)),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(pageCount) { iteration ->
                val color = if (currentPage == iteration) Color.White else Color.Gray
                Box(
                    modifier =
                        Modifier
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp),
                )
            }
        }
    }
}
