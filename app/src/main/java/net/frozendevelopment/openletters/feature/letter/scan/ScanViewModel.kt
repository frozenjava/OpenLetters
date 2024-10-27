package net.frozendevelopment.openletters.feature.letter.scan

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.usecase.CreateLetterUseCase
import net.frozendevelopment.openletters.usecase.LetterWithDetailsUseCase
import net.frozendevelopment.openletters.util.StatefulViewModel
import net.frozendevelopment.openletters.util.TextExtractorType
import java.io.File

@Immutable
data class ScanState(
    val isBusy: Boolean = false,
    val sender: String? = null,
    val recipient: String? = null,
    val documents: Map<DocumentId, Uri> = emptyMap(),
    val categories: List<Category> = emptyList(),
    val selectedCategories: Set<Category> = emptySet(),
) {
    val canLeaveSafely: Boolean
        get() = !isBusy && sender.isNullOrBlank() && recipient.isNullOrBlank() && documents.isEmpty()

    val isSavable: Boolean
        get() = documents.isNotEmpty()

    val categoryMap: Map<Category, Boolean>
        get() = categories.associateWith { category -> selectedCategories.contains(category) }
}

class ScanViewModel(
    letterToEdit: LetterId?,
    private val textExtractor: TextExtractorType,
    private val createLetter: CreateLetterUseCase,
    private val letterWithDetails: LetterWithDetailsUseCase,
    private val categoryQueries: net.frozendevelopment.openletters.data.sqldelight.CategoryQueries,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
): StatefulViewModel<ScanState>(ScanState()) {
    private val letterId: LetterId by lazy {
        if (letterToEdit?.value.isNullOrBlank()) {
            LetterId.random()
        } else {
            letterToEdit!!
        }
    }

    private val isEditing: Boolean = letterToEdit != null

    override fun load() {
        var state = ScanState(categories = categoryQueries.allCategories().executeAsList())

        if (isEditing) {
            val details = letterWithDetails(letterId) ?: return
            state = state.copy(
                sender = details.letter.sender,
                recipient = details.letter.recipient,
                documents = details.documents,
                selectedCategories = details.categories.toSet(),
            )
        }

        update { state }
    }

    fun getScanner(pageLimit: Int = 0): GmsDocumentScanner {
        return GmsDocumentScanning.getClient(GmsDocumentScannerOptions.Builder().apply {
            setGalleryImportAllowed(false)
            setResultFormats(RESULT_FORMAT_JPEG)
            setScannerMode(SCANNER_MODE_FULL)

            if (pageLimit > 0) {
                setPageLimit(pageLimit)
            }
        }.build())
    }

    fun importScannedDocuments(scanResult: GmsDocumentScanningResult?) {
        viewModelScope.launch(ioDispatcher) {
            val pages = scanResult?.pages
            if (pages.isNullOrEmpty()) {
                // TODO: Potentially set an error on the state?
                return@launch
            }

            // update the state to show the processing indicator and the scanned documents
            update { copy(documents = documents + pages.map { it.imageUri }.associateBy { DocumentId.random() }) }
        }
    }

    fun importScannedSender(scanResult: GmsDocumentScanningResult?) {
        viewModelScope.launch(ioDispatcher) {
            val pages = scanResult?.pages
            if (pages.isNullOrEmpty()) {
                // TODO: Potentially set an error on the state?
                return@launch
            }

            update { copy(isBusy = true) }

            val sender = textExtractor.extractFromImage(pages.first().imageUri)
            update { copy(
                isBusy = false,
                sender = sender
            )}
        }
    }

    fun importScannedRecipient(scanResult: GmsDocumentScanningResult?) {
        viewModelScope.launch(ioDispatcher) {
            val pages = scanResult?.pages
            if (pages.isNullOrEmpty()) {
                // TODO: Potentially set an error on the state?
                return@launch
            }

            update { copy(isBusy = true) }

            val recipient = textExtractor.extractFromImage(pages.first().imageUri)
            update { copy(
                isBusy = false,
                recipient = recipient
            )}
        }
    }

    fun toggleCategory(category: Category) = viewModelScope.launch {
        val selectedCategories = state.selectedCategories.toMutableSet()

        if (selectedCategories.contains(category)) {
            selectedCategories.remove(category)
        } else {
            selectedCategories.add(category)
        }

        update { copy(selectedCategories = selectedCategories) }
    }

    fun setSender(sender: String) = viewModelScope.launch {
        update { copy(sender = sender) }
    }

    fun setRecipient(recipient: String) = viewModelScope.launch {
        update { copy(recipient = recipient) }
    }

    fun removeDocument(documentId: DocumentId) = viewModelScope.launch {
        update { copy(
            documents = documents
                .toMutableMap()
                .apply { remove(documentId) }
        )}
    }

    suspend fun save(): Boolean {
        val saveState = state

        if (saveState.documents.isEmpty()) {
            // TODO: Set an error saying they cant save without having documents to save
            return false
        }

        update { copy(isBusy = true) }

        createLetter(
            sender = saveState.sender,
            recipient = saveState.recipient,
            categories = saveState.selectedCategories.map { it.id },
            documents = saveState.documents.filterValues { it != null } as Map<DocumentId, Uri>,
            letterId = letterId,
        )

        update { copy(isBusy = false) }
        return true
    }

    private fun cleanUpCache() {
        for (document in state.documents.values) {
            val path = document?.path ?: continue
            File(path).takeIf { it.exists() }?.delete()
        }
    }

    override fun onCleared() {
        cleanUpCache()
        super.onCleared()
    }
}
