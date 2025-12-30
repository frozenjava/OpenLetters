package net.frozendevelopment.openletters.feature.letter.scan.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.feature.letter.scan.ScanState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanAppBar(
    canNavigateBack: Boolean,
    state: ScanState,
    onBackClicked: () -> Unit,
    onSaveClicked: () -> Unit,
) {
    var showLeaveConfirmation: Boolean by remember { mutableStateOf(false) }

    BackHandler(enabled = !state.canLeaveSafely && canNavigateBack && !showLeaveConfirmation) {
        showLeaveConfirmation = true
    }

    if (showLeaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmation = false },
            title = { Text(stringResource(R.string.leave_without_saving)) },
            text = { Text(stringResource(R.string.unsaved_changes_warning)) },
            confirmButton = {
                Button(onClick = {
                    onBackClicked()
                    showLeaveConfirmation = false
                }) {
                    Text(stringResource(R.string.leave))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    CenterAlignedTopAppBar(
        title = { Text(text = "Import Letter") },
        navigationIcon = {
            if (!canNavigateBack) return@CenterAlignedTopAppBar

            IconButton(onClick = {
                if (state.canLeaveSafely) {
                    onBackClicked()
                } else {
                    showLeaveConfirmation = true
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back",
                )
            }
        },
        actions = {
            if (state.isBusy) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                TextButton(
                    onClick = onSaveClicked,
                    enabled = state.isSavable,
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        },
    )
}
