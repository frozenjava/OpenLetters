package net.frozendevelopment.openletters.feature.letter.list.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onNavDrawerClicked: () -> Unit,
    searchTerms: String,
    onSearchChanged: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                ),
    ) {
        IconButton(onClick = onNavDrawerClicked) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
        }

        BasicTextField(
            modifier =
                Modifier
                    .weight(1f, fill = true)
                    .padding(horizontal = 4.dp, vertical = 16.dp),
            singleLine = true,
            value = searchTerms,
            onValueChange = onSearchChanged,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        ) { innerTextField ->
            val placeHolderText =
                listOf(
                    "Tap to search for letters",
                    "\"Electric bill\"",
                    "\"123 Street Drive\"",
                    "\"Happy Birthday\"",
                    "\"Friday, October 31\"",
                )
            var currentIndex by remember { mutableIntStateOf(0) }
            if (searchTerms.isEmpty()) {
                AnimatedContent(
                    label = "Animated Placeholder",
                    targetState = placeHolderText[currentIndex],
                    transitionSpec = {
                        slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut()
                    },
                ) { targetText ->
                    Text(
                        text = targetText,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = .5f),
                        style = TextStyle(fontSize = 16.sp),
                    )
                }

                LaunchedEffect(Unit) {
                    while (this.isActive) {
                        delay(3000)
                        currentIndex = (currentIndex + 1) % placeHolderText.size
                    }
                }
            }

            innerTextField()
        }

        AnimatedVisibility(
            searchTerms.isNotBlank(),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            IconButton(onClick = { onSearchChanged("") }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = null)
            }
        }
    }
}

@Composable
private fun SearchBarPreview(searchTerms: String) {
    OpenLettersTheme {
        Surface {
            SearchBar(
                searchTerms = searchTerms,
                onNavDrawerClicked = {},
                onSearchChanged = {},
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun EmptySearchBar() {
    SearchBarPreview("")
}

@Composable
@PreviewLightDark
private fun FilledSearchBar() {
    SearchBarPreview("Some Search Terms")
}
