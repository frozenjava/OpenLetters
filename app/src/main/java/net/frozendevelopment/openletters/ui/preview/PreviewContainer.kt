package net.frozendevelopment.openletters.ui.preview

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
            content()
        }
    }
}
