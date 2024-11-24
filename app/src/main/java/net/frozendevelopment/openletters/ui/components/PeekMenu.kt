package net.frozendevelopment.openletters.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun PeekMenu(
    onDismissRequest: () -> Unit,
    menuContent: @Composable ColumnScope.() -> Unit,
    peekContent: @Composable () -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val animationDuration = 200
    var beginAnimation by remember { mutableStateOf(false) }

    val animatedColor by animateColorAsState(
        if (beginAnimation) Color.Black.copy(alpha = 0.75f) else Color.Transparent,
        animationSpec = tween(animationDuration, easing = LinearEasing),
        label = "color",
    )

    LaunchedEffect(Unit) {
        beginAnimation = true
    }

    BackHandler {
        onDismissRequest()
    }

    Popup(onDismissRequest = onDismissRequest) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .blur(100.dp)
                    .background(animatedColor)
                    .clickable(onClick = onDismissRequest),
        )

        AnimatedVisibility(
            visible = beginAnimation,
            enter = scaleIn(animationSpec = tween(animationDuration)) + fadeIn(animationSpec = tween(animationDuration)),
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
                    VerticalPeekMenu(
                        peekContent = peekContent,
                        menuContent = menuContent,
                    )
                } else {
                    HorizontalPeekMenu(
                        peekContent = peekContent,
                        menuContent = menuContent,
                    )
                }
            }
        }
    }
}

/**
 * Used for displaying on tablets or large displays
 */
@Composable
private fun HorizontalPeekMenu(
    peekContent: @Composable () -> Unit,
    menuContent: @Composable ColumnScope.() -> Unit,
) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxSize(.75f),
        ) {
            peekContent()
        }

        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp,
            modifier = Modifier.align(Alignment.CenterVertically),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp),
            ) {
                menuContent()
            }
        }
    }
}

/**
 * Used for displaying on compact displays
 */
@Composable
private fun VerticalPeekMenu(
    peekContent: @Composable () -> Unit,
    menuContent: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp,
            modifier =
                Modifier
                    .fillMaxWidth(.95f)
                    .fillMaxHeight(.5f),
        ) {
            peekContent()
        }

        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp),
            ) {
                menuContent()
            }
        }
    }
}
