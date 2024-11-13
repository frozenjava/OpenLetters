package net.frozendevelopment.openletters.feature.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object SettingsDestination

fun NavGraphBuilder.settings(
    navController: NavController,
    drawerState: DrawerState,
) {
    composable<SettingsDestination> {
        val viewModel: SettingsViewModel = koinViewModel()
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        Surface {
            SettingsView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onBackClicked = { navController.popBackStack() },
                onThemeChanged = viewModel::setTheme,
                onColorVariantChanged = viewModel::setVariant,
                onViewSourceClicked = {}
            )
        }
    }
}
