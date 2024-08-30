package net.frozendevelopment.mailshare.feature.scan

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
import net.frozendevelopment.mailshare.data.sqldelight.CategoryQueries
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category
import net.frozendevelopment.mailshare.usecase.CreateLetterUseCase
import net.frozendevelopment.mailshare.util.StatefulViewModel
import net.frozendevelopment.mailshare.util.TextExtractorType

@Immutable
data class ScanState(
    val isBusy: Boolean = false,
    val sender: String? = null,
    val recipient: String? = null,
    val scanTarget: ScanTarget = ScanTarget.LETTER,
    val documents: List<Uri> = emptyList(),
    val categories: Map<Category, Boolean> = emptyMap(),
) {
    enum class ScanTarget {
        SENDER, RECIPIENT, LETTER
    }

    val canLeaveSafely: Boolean
        get() = !isBusy && sender.isNullOrBlank() && recipient.isNullOrBlank() && documents.isEmpty()
}

class ScanViewModel(
    private val textExtractor: TextExtractorType,
    private val createLetter: CreateLetterUseCase,
    private val categoryQueries: CategoryQueries,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
): StatefulViewModel<ScanState>(ScanState()) {

    init {
        observe()
    }

    private fun observe() = viewModelScope.launch {
        categoryQueries.allCategories()
            .asFlow()
            .mapToList(ioDispatcher)
            .collect { categories ->
                update {
                    copy(categories = categories.associateWith {
                        this.categories.getOrDefault(it, false)
                    })
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
            update { copy(documents = pages.map { it.imageUri }) }
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

    suspend fun save(): Boolean {
        update { copy(isBusy = true) }

        createLetter(
            sender = state.sender,
            recipient = state.recipient,
            categories = state.categories.filter { it.value }.map { it.key.id },
            documents = emptyList(),
            threads = emptyList(),
        )

        update { copy(isBusy = false) }
        return true
    }
}
