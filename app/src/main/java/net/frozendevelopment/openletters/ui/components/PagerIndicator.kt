package net.frozendevelopment.openletters.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme

@Composable
fun PagerIndicator(
    modifier: Modifier = Modifier,
    currentPage: Int,
    pageCount: Int,
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.85f), shape = RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(pageCount) { iteration ->
            val color = if (currentPage == iteration) Color.White else Color.Gray
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp),
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun PagerIndicatorPreview() {
    OpenLettersTheme {
        Surface {
            PagerIndicator(currentPage = 1, pageCount = 3)
        }
    }
}
