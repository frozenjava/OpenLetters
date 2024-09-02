package net.frozendevelopment.mailshare.feature.scan

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

const val SCAN_FORM_ROUTE = "/letters/import"

fun NavController.openScanForm(options: NavOptions = NavOptions.Builder().build()) {
    navigate(SCAN_FORM_ROUTE, options)
}

fun NavGraphBuilder.scan(navController: NavController) {
    composable(SCAN_FORM_ROUTE) {
        Surface {
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            val viewModel: ScanViewModel = koinViewModel()
            val state by viewModel.stateFlow.collectAsStateWithLifecycle()

            val letterScanLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                    viewModel.importScannedDocuments(scanResult)
                }
            }

            val senderScanLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                    viewModel.importScannedSender(scanResult)
                }
            }

            val recipientScanLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                    viewModel.importScannedRecipient(scanResult)
                }
            }

            ScanFormView(
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                state = state,
                toggleCategory = viewModel::toggleCategory,
                setSender = viewModel::setSender,
                setRecipient = viewModel::setRecipient,
                openLetterScanner = {
                    val activity = context as? Activity
                    if (activity != null) {
                        viewModel.getScanner().getStartScanIntent(activity)
                            .addOnSuccessListener { intentSender ->
                                letterScanLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                            }
                            .addOnFailureListener {
                                Log.e("ScanNavigation", "Scanner failed to load")
                            }
                    }
                },
                openSenderScanner = {
                    val activity = context as? Activity
                    if (activity != null) {
                        viewModel.getScanner(pageLimit = 1).getStartScanIntent(activity)
                            .addOnSuccessListener { intentSender ->
                                senderScanLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                            }
                            .addOnFailureListener {
                                Log.e("ScanNavigation", "Scanner failed to load")
                            }
                    }
                },
                openRecipientScanner = {
                    val activity = context as? Activity
                    if (activity != null) {
                        viewModel.getScanner(pageLimit = 1).getStartScanIntent(activity)
                            .addOnSuccessListener { intentSender ->
                                recipientScanLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                            }
                            .addOnFailureListener {
                                Log.e("ScanNavigation", "Scanner failed to load")
                            }
                    }
                },
                onSaveClicked = {
                    coroutineScope.launch(Dispatchers.IO) {
                        if (viewModel.save()) {
                            withContext(Dispatchers.Main) {
                                navController.navigateUp()
                            }
                        }
                    }
                },
                onBackClicked = navController::navigateUp,
                onDeleteDocumentClicked = viewModel::removeDocument
            )
        }
    }
}
