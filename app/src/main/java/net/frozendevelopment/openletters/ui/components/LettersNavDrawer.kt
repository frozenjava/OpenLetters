package net.frozendevelopment.openletters.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme

@Composable
fun LettersNavDrawer(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    goToMail: () -> Unit,
    goToManageCategories: () -> Unit,
    goToCreateCategory: () -> Unit,
    goToReminders: () -> Unit,
    goToCreateReminder: () -> Unit,
    goToSettings: () -> Unit,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        content = content,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    goToMail = goToMail,
                    goToManageCategories = goToManageCategories,
                    goToCreateCategory = goToCreateCategory,
                    goToReminders = goToReminders,
                    goToCreateReminder = goToCreateReminder,
                    goToSettings = goToSettings,
                )
            }
        },
    )
}

@Composable
private fun DrawerContent(
    goToMail: () -> Unit,
    goToManageCategories: () -> Unit,
    goToCreateCategory: () -> Unit,
    goToReminders: () -> Unit,
    goToCreateReminder: () -> Unit,
    goToSettings: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.letters),
            style = MaterialTheme.typography.titleMedium,
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.letter_box)) },
            selected = false,
            onClick = goToMail,
        )

        HorizontalDivider()

        Text(
            text = stringResource(R.string.categories),
            style = MaterialTheme.typography.titleMedium,
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.manage_categories)) },
            selected = false,
            onClick = goToManageCategories,
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.create_category)) },
            selected = false,
            onClick = goToCreateCategory,
        )

        HorizontalDivider()

        Text(
            text = stringResource(R.string.reminders),
            style = MaterialTheme.typography.titleMedium,
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.manage_reminders)) },
            selected = false,
            onClick = goToReminders,
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.create_reminder)) },
            selected = false,
            onClick = goToCreateReminder,
        )

        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.settings)) },
            selected = false,
            onClick = goToSettings,
        )

        Spacer(modifier = Modifier.weight(1f))

        VersionStamp(modifier = Modifier.fillMaxWidth())
    }
}

@Preview
@Composable
fun MailNavDrawerPreview() {
    OpenLettersTheme {
        Surface {
            DrawerContent(
                goToMail = {},
                goToManageCategories = {},
                goToCreateCategory = {},
                goToReminders = {},
                goToCreateReminder = {},
                goToSettings = {},
            )
        }
    }
}
