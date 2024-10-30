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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
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
    val isCreatingTranscript: Boolean = false,
    val transcript: String? = null,
    val categories: List<Category> = emptyList(),
    val selectedCategories: Set<Category> = emptySet(),
    val newDocuments: Map<DocumentId, Uri> = emptyMap(), // any new documents added before saving
    val existingDocuments: Map<DocumentId, Uri> = emptyMap(), // any existing documents when editing
) {
    val canLeaveSafely: Boolean
        get() = !isBusy && sender.isNullOrBlank() && recipient.isNullOrBlank() && documents.isEmpty()

    val isSavable: Boolean
        get() = documents.isNotEmpty()

    val categoryMap: Map<Category, Boolean>
        get() = categories.associateWith { category -> selectedCategories.contains(category) }

    // all documents for display in the UI
    val documents: Map<DocumentId, Uri>
        get() = existingDocuments + newDocuments
}

class ScanViewModel(
    letterToEdit: LetterId?,
    private val textExtractor: TextExtractorType,
    private val createLetter: CreateLetterUseCase,
    private val letterWithDetails: LetterWithDetailsUseCase,
    private val categoryQueries: CategoryQueries,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
): StatefulViewModel<ScanState>(
    initialState = ScanState(),
    loadStateStrategy = SharingStarted.Eagerly,
) {
    private var buildTranscriptJob: Job? = null
    private var collectionObserver: Job? = null

    private val letterId: LetterId by lazy {
        if (letterToEdit?.value.isNullOrBlank()) {
            LetterId.random()
        } else {
            letterToEdit!!
        }
    }

    private val isEditing: Boolean = letterToEdit != null

    override fun load() {
        var state = ScanState()

        if (isEditing) {
            val details = letterWithDetails(letterId) ?: return
            state = state.copy(
                sender = details.letter.sender,
                recipient = details.letter.recipient,
                transcript = details.letter.body,
                existingDocuments = details.documents,
                selectedCategories = details.categories.toSet(),
            )
        }

        update { state }
        observe()
    }

    private fun observe() {
        // i hate this but im not sure a better way to do it.
        // all abstractions i create seem worse.
        collectionObserver?.cancel()
        collectionObserver = viewModelScope.launch {
            categoryQueries.allCategories()
                .asFlow()
                .mapToList(ioDispatcher)
                .collectLatest { categories ->
                    update { copy(categories = categories) }
                }
        }
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
            update { copy(newDocuments = newDocuments + pages.map { it.imageUri }.associateBy { DocumentId.random() }) }

            // this can be done much better with a channel if not for time
            rebuildTranscript()
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

    fun setTranscript(transcript: String) = viewModelScope.launch {
        update { copy(transcript = transcript.takeIf { it.isNotBlank() }) }
    }

    fun removeDocument(documentId: DocumentId) = viewModelScope.launch {
        update { copy(
            newDocuments = newDocuments
                .toMutableMap()
                .apply { remove(documentId) },
            existingDocuments = existingDocuments
                .toMutableMap()
                .apply { remove(documentId) }
        )}

        rebuildTranscript()
    }

    suspend fun save(): Boolean {
        update { copy(isBusy = true) }

        // wait for the transcript to finish building if needed
        buildTranscriptJob?.join()

        val saveState = state

        if (saveState.documents.isEmpty()) {
            // TODO: Set an error saying they cant save without having documents to save
            update { copy(isBusy = false) }
            return false
        }

        createLetter(
            sender = saveState.sender,
            recipient = saveState.recipient,
            transcript = saveState.transcript,
            categories = saveState.selectedCategories.map { it.id },
            documents = saveState.documents,
            letterId = letterId,
        )

        update { copy(isBusy = false) }
        return true
    }

    private fun rebuildTranscript() {
        buildTranscriptJob?.cancel()
        buildTranscriptJob = viewModelScope.launch {
            val extractedText = state.documents
                .values
                .mapNotNull { textExtractor.extractFromImage(it) }
                .filter { it.isNotBlank() }
                .joinToString("\n\n")

            update { copy(transcript = extractedText, isCreatingTranscript = false) }
        }
    }

    /**
     * We only want to delete documents under `newDocuments` because there are stored in the cache folder.
     * When a letter is saved, the documents are copied out of the cache folder and into a persisted folder
     * however, they are not deleted from the cache folder at that time. In addition, if a letter
     * is not saved the documents still exist in the cache folder and become orphaned.
     * This handles cleaning up the cache folder for both scenarios so we dont take up additional storage
     */
    private fun cleanUpCache() {
        for (document in state.newDocuments.values) {
            val path = document.path ?: continue
            File(path).takeIf { it.exists() }?.delete()
        }
    }

    override fun onCleared() {
        cleanUpCache()
        super.onCleared()
    }
}
