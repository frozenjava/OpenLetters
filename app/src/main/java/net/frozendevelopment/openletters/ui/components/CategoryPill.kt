package net.frozendevelopment.openletters.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.data.sqldelight.ReminderInfo
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.extensions.contrastColor
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun CategoryPill(
    modifier: Modifier = Modifier,
    category: Category,
    isSelected: Boolean,
    onToggle: (() -> Unit)? = null,
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
    color: Color,
    isSelected: Boolean,
    onToggle: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val buttonColor = if (isSelected)
        color
    else
        lerp(color, Color.Gray, .5f)

    CategoryPill(
        modifier = modifier
            .scale(scale.value)
            .clip(ButtonDefaults.shape)
            .let {
                if (onToggle == null) return@let it

                it.clickable {
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
            },
        color = buttonColor,
    ) {
        if (isSelected) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = buttonColor.contrastColor,
            )
        }
        Text(
            text = label,
            color = buttonColor.contrastColor,
            style = MaterialTheme.typography.labelLarge,
        )
    }

//    Row(
//        modifier = modifier
//            .scale(scale.value)
//            .background(
//                color = buttonColor,
//                shape = ButtonDefaults.shape
//            )
//            .clip(ButtonDefaults.shape)
//            .clickable {
//                onToggle()
//                coroutineScope.launch {
//                    scale.animateTo(
//                        0.90f,
//                        animationSpec = tween(100)
//                    )
//                    scale.animateTo(
//                        1f,
//                        animationSpec = spring(
//                            dampingRatio = Spring.DampingRatioHighBouncy,
//                            stiffness = Spring.StiffnessLow
//                        )
//                    )
//                }
//            }
//            .padding(ButtonDefaults.ContentPadding),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceAround
//    ) {
//        if (isSelected) {
//            Icon(
//                modifier = Modifier.size(16.dp),
//                imageVector = Icons.Default.Check,
//                contentDescription = "Selected",
//                tint = getContrastColor(backgroundColor = buttonColor),
//            )
//        }
//        Text(
//            text = label,
//            color = getContrastColor(backgroundColor = buttonColor),
//            style = MaterialTheme.typography.labelLarge,
//        )
//    }
}

@Composable
fun CategoryPill(
    modifier: Modifier = Modifier,
    color: Color,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .background(
                color = color,
                shape = ButtonDefaults.shape
            )
            .clip(ButtonDefaults.shape)
            .padding(ButtonDefaults.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        content()
    }
}

@Preview
@Composable
private fun CategoryPillPreview() {
    CategoryPill(
        label = "Some Label",
        color = Color(111111),
        isSelected = false,
        onToggle = {}
    )
}

@Preview
@Composable
private fun SelectedCategoryPillPreview() {
    CategoryPill(
        label = "Some Label",
        color = Color(111111),
        isSelected = true,
        onToggle = {}
    )
}
