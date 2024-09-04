package net.frozendevelopment.mailshare.feature.category

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import net.frozendevelopment.mailshare.feature.category.form.CATEGORY_FORM_ROUTE
import net.frozendevelopment.mailshare.feature.category.form.CategoryFormView
import net.frozendevelopment.mailshare.feature.category.form.CategoryFormViewModel
import net.frozendevelopment.mailshare.feature.category.manage.MANAGE_CATEGORY_ROUTE
import net.frozendevelopment.mailshare.feature.category.manage.ManageCategoryView
import net.frozendevelopment.mailshare.feature.category.manage.ManageCategoryViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.categories(
    navController: NavController,
) {
    composable(CATEGORY_FORM_ROUTE) {
        val viewModel: CategoryFormViewModel = koinViewModel()
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()

        CategoryFormView(
            state = state,
            onLabelChanged = viewModel::setLabel,
            onColorChanged = viewModel::setColor,
            onBackClicked = navController::popBackStack,
            onSaveClicked = {
                coroutineScope.launch {
                    viewModel.save()
                    navController.popBackStack()
                }
            },
        )
    }
    composable(MANAGE_CATEGORY_ROUTE) {
        val viewModel: ManageCategoryViewModel = koinViewModel()
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(viewModel) {
            viewModel.load()
        }

        ManageCategoryView(
            state = state,
            openNavigationDrawer = { /*TODO*/ },
            editCategoryClicked = { navController.navigate(CATEGORY_FORM_ROUTE) },
            onDeleteClicked = { /*TODO*/ },
        )
    }
}
