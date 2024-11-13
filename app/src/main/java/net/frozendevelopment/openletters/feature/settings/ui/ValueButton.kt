package net.frozendevelopment.openletters.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun ValueButton(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    value: @Composable () -> Unit,
    onClick: () -> Unit,
    rightContent: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                title()
            }

            ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                value()
            }
        }
        rightContent()
    }
}
