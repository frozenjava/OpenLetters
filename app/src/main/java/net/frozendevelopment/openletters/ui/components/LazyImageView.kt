package net.frozendevelopment.openletters.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import java.io.File

private fun loadImage(
    context: Context,
    uri: Uri,
): ImageBitmap? {
    try {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            ImageDecoder
                .decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                .asImageBitmap()
        } else {
            val path = uri.path ?: return null
            val file = File(path)
            BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
        }
    } catch (e: Exception) {
        Log.e("LazyImageView", "loadImage: $uri:", e)
        return null
    }
}

@Composable
fun LazyImageView(
    modifier: Modifier = Modifier,
    uri: Uri,
) {
    val context = LocalContext.current
    var imageBitmap: ImageBitmap? by remember { mutableStateOf(null) }

    LaunchedEffect(uri) {
        imageBitmap = loadImage(context, uri)
    }

    if (imageBitmap != null) {
        Image(
            modifier = modifier,
            bitmap = imageBitmap!!,
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )
    } else {
        BrokenImageView(modifier = modifier)
    }
}

@Composable
fun BrokenImageView(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.small),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Icon(imageVector = Icons.Filled.BrokenImage, contentDescription = "Can not find image")
    }
}
