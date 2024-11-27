package net.frozendevelopment.openletters.mock

import android.net.Uri
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.util.DocumentManagerType

class DocumentManagerMock : DocumentManagerType {
    var deleteCallCount = 0
        private set

    var persistCallCount = 0
        private set

    var getCallCount = 0
        private set

    var deleteHandler: (DocumentId) -> Unit = { }
    var persistHandler: (Uri, DocumentId) -> Uri = { _, _ -> Uri.EMPTY }
    var getHandler: (DocumentId) -> Uri? = { null }

    override fun delete(documentId: DocumentId) {
        deleteCallCount++
        deleteHandler(documentId)
    }

    override fun persist(
        cacheUri: Uri,
        documentId: DocumentId,
    ): Uri {
        persistCallCount++
        return persistHandler(cacheUri, documentId)
    }

    override fun get(documentId: DocumentId): Uri? {
        getCallCount++
        return getHandler(documentId)
    }
}
