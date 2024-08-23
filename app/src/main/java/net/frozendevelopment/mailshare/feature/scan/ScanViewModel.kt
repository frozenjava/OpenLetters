package net.frozendevelopment.mailshare.feature.scan

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.frozendevelopment.mailshare.util.StatefulViewModel

@Immutable
data class ScanState(
    val isBusy: Boolean = false,
    val sender: String? = null,
    val recipient: String? = null,
    val letter: String = "",
    val scanTarget: ScanTarget = ScanTarget.LETTER
) {
    enum class ScanTarget {
        SENDER, RECIPIENT, LETTER
    }

    val canLeaveSafely: Boolean
        get() = !isBusy && sender.isNullOrBlank() && recipient.isNullOrBlank() && letter.isBlank()
}

class ScanViewModel: StatefulViewModel<ScanState>(ScanState()) {

    fun setScanTarget(target: ScanState.ScanTarget) {
        viewModelScope.launch {
            update { copy(scanTarget = target) }
        }
    }

    fun save() {
        viewModelScope.launch {
            update { copy(isBusy = true) }
        }
    }

}