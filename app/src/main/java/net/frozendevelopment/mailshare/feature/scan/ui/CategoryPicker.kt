package net.frozendevelopment.mailshare.feature.scan.ui

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.launch
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category

@Composable
fun CategoryPicker(
    modifier: Modifier = Modifier,
    categories: Map<Category, Boolean>,
    toggleCategory: (Category) -> Unit,
    onCreateClicked: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

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
                val scale = remember { Animatable(1f) }
                val isSelected = categories.getOrDefault(category, false)
                val buttonColor = if (isSelected)
                    Color(category.color).copy(alpha = 1f)
                else
                    Color(category.color).copy(alpha = .5f)

                Row(
                    modifier = Modifier
                        .scale(scale.value)
                        .background(
                            color = buttonColor,
                            shape = ButtonDefaults.shape
                        )
                        .clickable {
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

                            toggleCategory(category)
                        }
                        .padding(ButtonDefaults.ContentPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn(animationSpec = tween(500)) +
                                scaleIn(
                                    animationSpec = tween(500),
                                    initialScale = 0f
                                ),
                        exit = fadeOut(animationSpec = tween(500)) +
                                scaleOut(
                                    animationSpec = tween(500),
                                    targetScale = 0f
                                )
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Selected")
                    }
                    Text(
                        text = category.label,
                        color = getContrastColor(backgroundColor = buttonColor),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
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

fun getContrastColor(backgroundColor: Color): Color {
    return if (backgroundColor.luminance() > 0.5) Color.Black else Color.White
}
