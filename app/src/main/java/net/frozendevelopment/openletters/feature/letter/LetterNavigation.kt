package net.frozendevelopment.openletters.feature.letter

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.frozendevelopment.openletters.feature.category.form.CategoryFormDestination
import net.frozendevelopment.openletters.feature.category.form.CategoryFormMode
import net.frozendevelopment.openletters.feature.letter.detail.LetterDetailDestination
import net.frozendevelopment.openletters.feature.letter.detail.LetterDetailView
import net.frozendevelopment.openletters.feature.letter.detail.LetterDetailViewModel
import net.frozendevelopment.openletters.feature.letter.image.ImageDestination
import net.frozendevelopment.openletters.feature.letter.image.ImageView
import net.frozendevelopment.openletters.feature.letter.list.LetterListDestination
import net.frozendevelopment.openletters.feature.letter.list.LetterListView
import net.frozendevelopment.openletters.feature.letter.list.LetterListViewModel
import net.frozendevelopment.openletters.feature.letter.scan.ScanLetterDestination
import net.frozendevelopment.openletters.feature.letter.scan.ScanLetterView
import net.frozendevelopment.openletters.feature.letter.scan.ScanViewModel
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailDestination
import net.frozendevelopment.openletters.feature.reminder.form.ReminderFormDestination
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.letters(
    navController: NavController,
    drawerState: DrawerState,
) {
    composable<ImageDestination>(
        enterTransition = { fadeIn() + scaleIn() },
        exitTransition = { fadeOut() + scaleOut() },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        val destination = it.toRoute<ImageDestination>()
        Surface(
            color = Color.Black,
            contentColor = Color.White,
        ) {
            ImageView(
                modifier = Modifier.fillMaxSize(),
                uri = Uri.parse(destination.uri),
                onBackClick = navController::popBackStack,
            )
        }
    }
    composable<LetterListDestination> {
        val coroutineScope = rememberCoroutineScope()
        val viewModel: LetterListViewModel = koinViewModel()
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        Surface {
            LetterListView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onNavDrawerClicked = {
                    coroutineScope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                },
                onScanClicked = { navController.navigate(ScanLetterDestination()) },
                toggleCategory = viewModel::toggleCategory,
                setSearchTerms = viewModel::setSearchTerms,
                openLetter = { id, edit ->
                    if (edit) {
                        navController.navigate(ScanLetterDestination(id))
                    } else {
                        navController.navigate(LetterDetailDestination(id))
                    }
                },
                onDeleteLetterClicked = viewModel::delete,
                onReminderClicked = { id, edit ->
                    if (edit) {
                        navController.navigate(ReminderDetailDestination(id))
                    } else {
                        navController.navigate(ReminderDetailDestination(id))
                    }
                },
                onCreateReminderClicked = { navController.navigate(ReminderFormDestination(preselectedLetters = it)) },
            )
        }
    }
    composable<LetterDetailDestination>(
        typeMap = LetterDetailDestination.typeMap,
    ) { backStackEntry ->
        val destination = backStackEntry.toRoute<LetterDetailDestination>()
        val viewModel: LetterDetailViewModel = koinViewModel { parametersOf(destination.letterId) }
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        Surface {
            LetterDetailView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onEditClicked = { navController.navigate(ScanLetterDestination(destination.letterId)) },
                onCreateReminderClicked = { navController.navigate(ReminderFormDestination(preselectedLetters = listOf(destination.letterId))) },
                onBackClicked = navController::popBackStack,
                onImageClick = { uri -> navController.navigate(ImageDestination(uri.toString())) },
            )
        }
    }
    composable<ScanLetterDestination>(
        typeMap = ScanLetterDestination.typeMap,
    ) { backStackEntry ->
        val destination = backStackEntry.toRoute<ScanLetterDestination>()
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val viewModel: ScanViewModel = koinViewModel { parametersOf(destination.letterId) }
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

        Surface {
            ScanLetterView(
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                state = state,
                canNavigateBack = destination.canNavigateBack,
                toggleCategory = viewModel::toggleCategory,
                setSender = viewModel::setSender,
                setRecipient = viewModel::setRecipient,
                setTranscript = viewModel::setTranscript,
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
                                if (destination.canNavigateBack) {
                                    navController.navigateUp()
                                } else {
                                    navController.navigate(LetterListDestination) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }
                    }
                },
                onBackClicked = navController::navigateUp,
                onDeleteDocumentClicked = viewModel::removeDocument,
                onCreateCategoryClicked = { navController.navigate(CategoryFormDestination(CategoryFormMode.Create)) }
            )
        }
    }
}
