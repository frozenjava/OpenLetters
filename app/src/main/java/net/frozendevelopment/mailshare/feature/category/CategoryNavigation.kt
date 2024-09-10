package net.frozendevelopment.mailshare.feature.category

import androidx.compose.material3.DrawerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import net.frozendevelopment.mailshare.feature.category.form.CategoryFormDestination
import net.frozendevelopment.mailshare.feature.category.form.CategoryFormMode
import net.frozendevelopment.mailshare.feature.category.form.CategoryFormModeType
import net.frozendevelopment.mailshare.feature.category.form.CategoryFormView
import net.frozendevelopment.mailshare.feature.category.form.CategoryFormViewModel
import net.frozendevelopment.mailshare.feature.category.manage.MANAGE_CATEGORY_ROUTE
import net.frozendevelopment.mailshare.feature.category.manage.ManageCategoryView
import net.frozendevelopment.mailshare.feature.category.manage.ManageCategoryViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.reflect.typeOf

fun NavGraphBuilder.categories(
    navController: NavController,
    drawerState: DrawerState,
) {
    composable<CategoryFormDestination>(
        typeMap = mapOf(typeOf<CategoryFormMode>() to CategoryFormModeType),
    ) { backStackEntry ->
        val destination = backStackEntry.toRoute<CategoryFormDestination>()
        val viewModel: CategoryFormViewModel = koinViewModel { parametersOf(destination.mode) }
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
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(viewModel) {
            viewModel.load()
        }

        Surface {
            ManageCategoryView(
                state = state,
                openNavigationDrawer = {
                    coroutineScope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                },
                editCategoryClicked = { categoryId ->
                    val mode = if (categoryId == null) {
                        CategoryFormMode.Create
                    } else {
                        CategoryFormMode.Edit(categoryId)
                    }
                    navController.navigate(CategoryFormDestination(mode = mode))
                },
                onDeleteClicked = viewModel::delete,
                onMove = viewModel::onMove,
            )
        }

    }
}
