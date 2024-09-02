package net.frozendevelopment.mailshare.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import java.io.File

private fun loadImage(
    context: Context,
    uri: Uri,
): ImageBitmap? {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        ImageDecoder
            .decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            .asImageBitmap()
    } else {
        val path = uri.path ?: return null
        val file = File(path)
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
    }
}

@Composable
fun ImageViewer(
    modifier: Modifier = Modifier,
    uri: Uri,
) {
    val context = LocalContext.current
    val imageBitmap = loadImage(context, uri)

    if (imageBitmap != null) {
        Image(
            modifier = modifier,
            bitmap = imageBitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
    } else {
        // TODO: Display broken image icon
    }
}
