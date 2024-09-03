package net.frozendevelopment.mailshare.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category

@Composable
fun CategoryPill(
    modifier: Modifier = Modifier,
    category: Category,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    CategoryPill(
        modifier = modifier,
        label = category.label,
        color = category.color,
        isSelected = isSelected,
        onToggle = onToggle
    )
}

@Composable
fun CategoryPill(
    modifier: Modifier = Modifier,
    label: String,
    color: Long,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val buttonColor = if (isSelected)
        Color(color).copy(alpha = 1f)
    else
        Color(color).copy(alpha = .5f)

    Row(
        modifier = Modifier
            .scale(scale.value)
            .background(
                color = buttonColor,
                shape = ButtonDefaults.shape
            )
            .clip(ButtonDefaults.shape)
            .clickable {
                onToggle()
                coroutineScope.launch {
                    scale.animateTo(
                        0.90f,
                        animationSpec = tween(100)
                    )
                    scale.animateTo(
                        1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioHighBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            }
            .padding(ButtonDefaults.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        if (isSelected) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = getContrastColor(backgroundColor = buttonColor),
            )
        }
        Text(
            text = label,
            color = getContrastColor(backgroundColor = buttonColor),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

private fun getContrastColor(backgroundColor: Color): Color {
    return if (backgroundColor.luminance() > 0.5) Color.Black else Color.White
}

@Preview
@Composable
private fun CategoryPillPreview() {
    CategoryPill(
        label = "Some Label",
        color = 111111,
        isSelected = false,
        onToggle = {}
    )
}

@Preview
@Composable
private fun SelectedCategoryPillPreview() {
    CategoryPill(
        label = "Some Label",
        color = 111111,
        isSelected = true,
        onToggle = {}
    )
}
