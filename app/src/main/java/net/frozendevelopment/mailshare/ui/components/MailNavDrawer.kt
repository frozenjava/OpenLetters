package net.frozendevelopment.mailshare.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

@Composable
fun MailNavDrawer(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    goToMail: () -> Unit,
    goToThreads: () -> Unit,
    goToManageCategories: () -> Unit,
    goToCreateCategory: () -> Unit,
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
                    goToThreads = goToThreads,
                    goToManageCategories = goToManageCategories,
                    goToCreateCategory = goToCreateCategory
                )
            }
        },
    )
}

@Composable
private fun DrawerContent(
    goToMail: () -> Unit,
    goToThreads: () -> Unit,
    goToManageCategories: () -> Unit,
    goToCreateCategory: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ){
        Text(
            text = "Your Mail",
            style = MaterialTheme.typography.titleMedium
        )

        NavigationDrawerItem(
            label = { Text("Mail Box") },
            selected = false,
            onClick = goToMail
        )

        NavigationDrawerItem(
            label = { Text("Threads") },
            selected = false,
            onClick = goToThreads
        )

        HorizontalDivider()

        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium
        )

        NavigationDrawerItem(
            label = { Text("Manage Categories") },
            selected = false,
            onClick = goToManageCategories
        )

        NavigationDrawerItem(
            label = { Text("Create Category") },
            selected = false,
            onClick = goToCreateCategory
        )

        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text("Settings") },
            selected = false,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun MailNavDrawerPreview() {
    MailShareTheme {
        Surface {
            DrawerContent(
                goToMail = {},
                goToThreads = {},
                goToManageCategories = {},
                goToCreateCategory = {},
            )
        }
    }

}