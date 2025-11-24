package net.frozendevelopment.openletters.ui.preview

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import net.frozendevelopment.openletters.ui.navigation.NavHost
import net.frozendevelopment.openletters.ui.navigation.PreviewNavigator
import net.frozendevelopment.openletters.ui.navigation.rememberNavigationState
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme

@Composable
@VisibleForTesting
fun PreviewContainer(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    OpenLettersTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
    ) {
        Surface {
            NavHost(
                drawerState = rememberDrawerState(DrawerValue.Closed),
                navigationState = rememberNavigationState(object : NavKey {}, setOf()),
                navigator = PreviewNavigator(),
            ) {
                content()
            }
        }
    }
}
