package net.frozendevelopment.openletters.feature.letter.image

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.ui.components.LazyImageView

@Serializable
data class ImageDestination(
    val uri: String
)

@Composable
fun ImageView(
    modifier: Modifier = Modifier,
    uri: Uri,
    onBackClick: () -> Unit,
) {
    val activity = LocalContext.current as? Activity
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    var offset by remember { mutableStateOf(Offset.Zero) }
    var zoom by remember { mutableFloatStateOf(1f) }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.window?.let { window ->
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }

        onDispose {
            // lock the app to portrait for phone users
            if (windowSizeClass.windowWidthSizeClass ==  WindowWidthSizeClass.COMPACT) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            activity?.window?.let { window ->
                val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    Box(modifier = modifier.safeContentPadding()) {
        LazyImageView(
            uri = uri,
            modifier = Modifier
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { },
                        onDoubleTap = { tapOffset ->
                            zoom = if (zoom == 1f) 2f else 1f
                            offset = tapOffset.fromDoubleTap(zoom, size)
                        },
                        onLongPress = {
                            zoom = 1f
                            offset = Offset.Zero
                        },
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { centroid, pan, zoomChange, _ ->
                            offset = offset.new(centroid, pan, zoom, zoomChange, size)
                            zoom = maxOf(1f, zoom * zoomChange)
                        }
                    )
                }
                .graphicsLayer {
                    translationX = -offset.x * zoom
                    translationY = -offset.y * zoom
                    scaleX = zoom
                    scaleY = zoom
                    transformOrigin = TransformOrigin(0f, 0f)
                }
                .aspectRatio(1f),
        )

        TextButton(
            modifier = Modifier
                .align(Alignment.TopEnd),
            onClick = onBackClick,
        ) {
            Text(stringResource(R.string.close))
        }
    }
}

fun Offset.new(
    centroid: Offset,
    pan: Offset,
    zoom: Float,
    gestureZoom: Float,
    size: IntSize
): Offset {
    val newScale = maxOf(1f, zoom * gestureZoom)
    val newOffset = (this + centroid / zoom) - (centroid / newScale + pan / zoom)
    return Offset(
        newOffset.x.coerceIn(0f, (size.width / zoom) * (zoom - 1f)),
        newOffset.y.coerceIn(0f, (size.height / zoom) * (zoom - 1f))
    )
}

fun Offset.fromDoubleTap(
    zoom: Float,
    size: IntSize,
): Offset = Offset(
    x.coerceIn(0f, (size.width / zoom) * (zoom - 1f)),
    y.coerceIn(0f, (size.height / zoom) * (zoom - 1f))
)
