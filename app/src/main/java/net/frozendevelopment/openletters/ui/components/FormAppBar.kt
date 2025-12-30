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
import androidx.compose.ui.res.stringResource
import net.frozendevelopment.openletters.R

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

    BackHandler(enabled = isSavable && !showLeaveConfirmation) {
        showLeaveConfirmation = true
    }

    if (showLeaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmation = false },
            title = { Text(text = stringResource(R.string.leave_without_saving)) },
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
                    contentDescription = stringResource(R.string.back_button),
                )
            }
        },
        actions = {
            TextButton(
                onClick = onSaveClicked,
                enabled = isSavable,
            ) {
                Text(stringResource(R.string.save))
            }

            actions?.invoke(this)
        },
    )
}
