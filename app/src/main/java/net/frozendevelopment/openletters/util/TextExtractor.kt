package net.frozendevelopment.openletters.util

import android.app.Application
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface TextExtractorType {
    suspend fun extractFromImage(uri: Uri): String?
}

class TextExtractor(
    private val application: Application,
) : TextExtractorType {
    private val textRecognizer: TextRecognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    override suspend fun extractFromImage(uri: Uri): String? {
        return suspendCancellableCoroutine { continuation ->
            val image =
                try {
                    InputImage.fromFilePath(application, uri)
                } catch (e: IOException) {
                    return@suspendCancellableCoroutine
                }

            textRecognizer
                .process(image)
                .addOnSuccessListener { visionText ->
                    continuation.resume(visionText.text)
                }.addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
}
