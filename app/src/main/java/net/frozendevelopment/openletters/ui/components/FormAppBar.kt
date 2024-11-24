package net.frozendevelopment.openletters.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.RowScope
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
fun FormAppBar(
    isSavable: Boolean,
    onBackClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    title: @Composable () -> Unit,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    var showLeaveConfirmation: Boolean by remember { mutableStateOf(false) }

    BackHandler(enabled = isSavable) {
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
            },
        )
    }

    CenterAlignedTopAppBar(
        title = title,
        navigationIcon = {
            IconButton(onClick = {
                if (!isSavable) {
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
            TextButton(
                onClick = onSaveClicked,
                enabled = isSavable,
            ) {
                Text("Save")
            }

            actions?.invoke(this)
        },
    )
}
