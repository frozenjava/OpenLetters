package net.frozendevelopment.openletters.feature.letter.scan.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanAppBar(
    canNavigateBack: Boolean,
    canLeaveSafely: Boolean,
    isSavable: Boolean,
    onBackClicked: () -> Unit,
    onSaveClicked: () -> Unit,
) {
    var showLeaveConfirmation: Boolean by remember { mutableStateOf(false) }

    BackHandler(enabled = !canLeaveSafely && canNavigateBack) {
        showLeaveConfirmation = true
    }

    if (showLeaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmation = false },
            title = { Text(text = "Leave without saving?") },
            text = { Text("If you leave without saving, your changes will be lost.") },
            confirmButton = {
                Button(onClick = onBackClicked) {
                    Text("Leave")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    CenterAlignedTopAppBar(
        title = { Text(text = "Import Letter") },
        navigationIcon = {
            if (!canNavigateBack) return@CenterAlignedTopAppBar

            IconButton(onClick = {
                if (canLeaveSafely) {
                    onBackClicked()
                } else {
                    showLeaveConfirmation = true
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back"
                )
            }
        },
        actions = {
            TextButton(
                onClick = onSaveClicked,
                enabled = isSavable
            ) {
                Text("Save")
            }
        }
    )
}