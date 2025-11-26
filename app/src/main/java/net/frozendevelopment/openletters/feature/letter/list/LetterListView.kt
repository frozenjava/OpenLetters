package net.frozendevelopment.openletters.feature.letter.list

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.extensions.navigation
import net.frozendevelopment.openletters.feature.letter.detail.LetterDetailDestination
import net.frozendevelopment.openletters.feature.letter.list.ui.EmptyListView
import net.frozendevelopment.openletters.feature.letter.list.ui.LetterList
import net.frozendevelopment.openletters.feature.letter.scan.ScanLetterDestination
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailDestination
import net.frozendevelopment.openletters.feature.reminder.form.ReminderFormDestination
import net.frozendevelopment.openletters.ui.navigation.ListDetailScene.Companion.listPane
import net.frozendevelopment.openletters.ui.navigation.LocalDrawerState
import net.frozendevelopment.openletters.ui.navigation.LocalNavigator
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module

@Serializable
data object LetterListDestination : NavKey

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3AdaptiveApi::class)
fun Module.letterListNavigation() = navigation<LetterListDestination>(
    metadata = NavDisplay.transitionSpec {
        EnterTransition.None togetherWith ExitTransition.None
    } + SupportingPaneSceneStrategy.mainPane()
) { route ->
    val drawerState = LocalDrawerState.current
    val navigator = LocalNavigator.current

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
            onScanClicked = { navigator.navigate(ScanLetterDestination()) },
            toggleCategory = viewModel::toggleCategory,
            setSearchTerms = viewModel::setSearchTerms,
            openLetter = { id, edit ->
                if (edit) {
                    navigator.navigate(ScanLetterDestination(id))
                } else {
                    navigator.navigate(LetterDetailDestination(id))
                }
            },
            onDeleteLetterClicked = viewModel::delete,
            onReminderClicked = { id, edit ->
                if (edit) {
                    navigator.navigate(ReminderDetailDestination(id))
                } else {
                    navigator.navigate(ReminderDetailDestination(id))
                }
            },
            onCreateReminderClicked = { navigator.navigate(ReminderFormDestination(preselectedLetters = it)) },
        )
    }
}

@Composable
fun LetterListView(
    modifier: Modifier = Modifier,
    state: LetterListState,
    onNavDrawerClicked: () -> Unit,
    onScanClicked: () -> Unit,
    toggleCategory: (CategoryId?) -> Unit,
    setSearchTerms: (String) -> Unit,
    openLetter: (id: LetterId, edit: Boolean) -> Unit,
    onDeleteLetterClicked: (id: LetterId) -> Unit,
    onReminderClicked: (id: ReminderId, edit: Boolean) -> Unit,
    onCreateReminderClicked: (List<LetterId>) -> Unit,
) {
    if (state.isLoading) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (state.showEmptyListView) {
        EmptyListView(
            modifier = modifier,
            onScanClicked = onScanClicked,
        )
    } else {
        LetterList(
            modifier = modifier,
            state = state,
            onLetterClicked = openLetter,
            onDeleteLetterClicked = onDeleteLetterClicked,
            onNavDrawerClicked = onNavDrawerClicked,
            onScanClicked = onScanClicked,
            selectCategory = toggleCategory,
            setSearchTerms = setSearchTerms,
            onReminderClicked = onReminderClicked,
            onCreateReminderClicked = onCreateReminderClicked,
        )
    }
}

@Composable
private fun LetterListPreview(state: LetterListState) {
    OpenLettersTheme {
        Surface(
            modifier =
                Modifier
                    .fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            LetterListView(
                modifier = Modifier.padding(vertical = 8.dp),
                state = state,
                onNavDrawerClicked = {},
                onScanClicked = {},
                toggleCategory = {},
                setSearchTerms = {},
                openLetter = { _, _ -> },
                onDeleteLetterClicked = {},
                onReminderClicked = { _, _ -> },
                onCreateReminderClicked = {},
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun EmptyPreview() {
    LetterListPreview(
        state = LetterListState(showEmptyListView = true, isLoading = false),
    )
}

@Composable
@PreviewLightDark
private fun LoadingPreview() {
    LetterListPreview(
        state = LetterListState(isLoading = true),
    )
}
