package net.frozendevelopment.mailshare.feature.mail.list.ui

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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import net.frozendevelopment.mailshare.extensions.contrastColor
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

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
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium
            ),
    ) {
        IconButton(onClick = onNavDrawerClicked) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
        }

        BasicTextField(
            modifier = Modifier
                .weight(1f, fill = true)
                .padding(horizontal = 4.dp, vertical = 16.dp),
            singleLine = true,
            value = searchTerms,
            onValueChange = onSearchChanged,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSecondaryContainer),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSecondaryContainer),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        ) { innerTextField ->
            val placeHolderText = listOf(
                "Tap to search your mail",
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
                    }
                ) { targetText ->
                    Text(
                        text = targetText,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = .5f),
                        style = TextStyle(fontSize = 16.sp)
                    )
                }

                LaunchedEffect(Unit) {
                    while(this.isActive) {
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
            exit = fadeOut() + scaleOut()
        ) {
            IconButton(onClick = { onSearchChanged("") }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = null)
            }
        }

    }
}

@Composable
private fun SearchBarPreview(darkTheme: Boolean, searchTerms: String) {
    MailShareTheme(darkTheme = darkTheme) {
        Surface {
            SearchBar(
                searchTerms = searchTerms,
                onNavDrawerClicked = {},
                onSearchChanged = {}
            )
        }
    }
}

@Composable
@Preview
private fun EmptySearchBarLight() {
    SearchBarPreview(false, "")
}

@Composable
@Preview
private fun EmptySearchBarDark() {
    SearchBarPreview(true, "")
}

@Composable
@Preview
private fun FilledSearchBarLight() {
    SearchBarPreview(false, "Some Search Terms")
}

@Composable
@Preview
private fun FilledSearchBarDark() {
    SearchBarPreview(true, "Some Search Terms")
}