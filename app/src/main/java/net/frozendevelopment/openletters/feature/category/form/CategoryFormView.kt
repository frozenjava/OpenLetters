package net.frozendevelopment.openletters.feature.category.form

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import net.frozendevelopment.openletters.extensions.Random
import net.frozendevelopment.openletters.extensions.contrastColor
import net.frozendevelopment.openletters.ui.components.CategoryPill
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormView(
    state: CategoryFormState,
    onLabelChanged: (String) -> Unit,
    onColorChanged: (Color) -> Unit,
    onBackClicked: () -> Unit,
    onSaveClicked: () -> Unit,
) {
    val controller = rememberColorPickerController()
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CenterAlignedTopAppBar(
            title = { Text(text = state.title) },
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            },
            actions = {
                TextButton(
                    onClick = onSaveClicked,
                    enabled = state.isSavable,
                ) {
                    Text(text = "Save")
                }
            },
        )

        if (state.isBusy) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            return@Column
        }

        CategoryPill(
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            color = state.color,
        ) {
            BasicTextField(
                modifier = Modifier.padding(vertical = 24.dp),
                value = state.label,
                onValueChange = onLabelChanged,
                singleLine = true,
                interactionSource = interactionSource,
                keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Words),
                cursorBrush = SolidColor(state.color.contrastColor),
                textStyle =
                    MaterialTheme.typography.titleLarge.copy(
                        color = state.color.contrastColor,
                        textAlign = TextAlign.Center,
                    ),
            ) { innerTextField ->
                if (state.label.isBlank() && !isFocused) {
                    Text(
                        text = "Tap to type your label",
                        color = state.color.contrastColor,
                        style = MaterialTheme.typography.titleLarge,
                    )
                } else {
                    innerTextField()
                }
            }
        }

        HorizontalDivider()

        TextButton(
            modifier = Modifier.fillMaxWidth(.95f),
            onClick = {
                val color = Color.Random
                controller.selectByColor(color, true)
                onColorChanged(color)
            },
        ) {
            Text(text = "Randomize Color")
        }

        BrightnessSlider(
            modifier =
                Modifier
                    .fillMaxWidth(.95f)
                    .height(35.dp),
            controller = controller,
            borderRadius = 32.dp,
        )

        HsvColorPicker(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp),
            onColorChanged = { onColorChanged(it.color) },
            controller = controller,
            initialColor = state.color,
        )
    }
}

@Composable
private fun CategoryFormPreview(
    darkTheme: Boolean,
    state: CategoryFormState,
) {
    OpenLettersTheme(darkTheme) {
        Surface {
            CategoryFormView(
                state = state,
                onLabelChanged = {},
                onColorChanged = {},
                onBackClicked = {},
                onSaveClicked = {},
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun DarkPreview() {
    CategoryFormPreview(
        darkTheme = true,
        state =
            CategoryFormState(
                mode = CategoryFormMode.Create,
                label = "",
                color = Color(0xFF0F0FF0),
            ),
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun LightPreview() {
    CategoryFormPreview(
        darkTheme = false,
        state =
            CategoryFormState(
                mode = CategoryFormMode.Create,
                label = "",
                color = Color(0xFF0F0FF0),
            ),
    )
}
