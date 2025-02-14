package net.frozendevelopment.openletters.util

import android.content.Context
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.core.net.toUri
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import java.io.File

interface DocumentManagerType {
    fun delete(documentId: DocumentId)

    fun persist(
        cacheUri: Uri,
        documentId: DocumentId,
    ): Uri

    fun get(documentId: DocumentId): Uri?
}

fun DocumentManagerType.delete(documents: Collection<DocumentId>) {
    for (document in documents) {
        delete(document)
    }
}

fun DocumentManagerType.get(documents: Collection<DocumentId>): List<Uri> {
    return documents.mapNotNull { get(it) }
}

class DocumentManager(
    private val context: Context,
) : DocumentManagerType {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val documentDirectory: File by lazy {
        File(context.filesDir, "documents").also {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
    }

    private val DocumentId.file: File?
        get() = documentDirectory.resolve(value).takeIf { it.exists() }

    override fun delete(documentId: DocumentId) {
        documentId.file?.delete()
    }

    @Throws(IllegalStateException::class)
    override fun persist(
        cacheUri: Uri,
        documentId: DocumentId,
    ): Uri {
        val cachePath = cacheUri.path
        if (cachePath.isNullOrBlank() || !File(cachePath).exists()) {
            throw IllegalStateException("Cache file does not exist")
        }

        if (File(documentDirectory, documentId.value).exists()) {
            throw IllegalStateException("Document already exists")
        }

        val outputFile = File(documentDirectory, documentId.value)
        val cachedFile = File(cacheUri.path!!)

        cachedFile.copyTo(outputFile)

        return outputFile.toUri()
    }

    override fun get(documentId: DocumentId): Uri? {
        val document = File(documentDirectory, documentId.value)

        return if (document.exists()) {
            document.toUri()
        } else {
            null
        }
    }
}
