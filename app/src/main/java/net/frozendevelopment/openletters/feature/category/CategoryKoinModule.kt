package net.frozendevelopment.openletters.feature.category

import net.frozendevelopment.openletters.feature.category.form.CategoryFormDestination
import net.frozendevelopment.openletters.feature.category.form.CategoryFormViewModel
import net.frozendevelopment.openletters.feature.category.form.categoryFormNavigation
import net.frozendevelopment.openletters.feature.category.manage.ManageCategoryViewModel
import net.frozendevelopment.openletters.feature.category.manage.manageCategoryNavigation
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// @Module
// class CategoryKoinModule {
//    // @KoinViewModel
//    fun categoryFormViewModel(
//        @InjectedParam mode: CategoryFormMode,
//        upsertCategoryUseCase: UpsertCategoryUseCase,
//        categoryQueries: CategoryQueries,
//    ) = CategoryFormViewModel(
//        mode = mode,
//        upsertCategoryUseCase = upsertCategoryUseCase,
//        categoryQueries = categoryQueries,
//    )
//
//    // @KoinViewModel
//    fun manageCategoryViewModel(
//        saveCategoryOrderUseCase: SaveCategoryOrderUseCase,
//        categoryQueries: CategoryQueries,
//    ) = ManageCategoryViewModel(saveCategoryOrderUseCase, categoryQueries)
//
//    companion object {
//        @OptIn(KoinExperimentalAPI::class)
//        val navigationModule = module {
//            categoryFormNavigation()
//            manageCategoryNavigation()
//        }
//    }
// }

val categoryKoinModule =
    module {
        categoryFormNavigation()
        manageCategoryNavigation()
        viewModel { (mode: CategoryFormDestination.Mode) ->
            CategoryFormViewModel(
                mode = mode,
                upsertCategoryUseCase = get(),
                categoryQueries = get(),
            )
        }

        viewModel {
            ManageCategoryViewModel(
                saveCategoryOrder = get(),
                categoryQueries = get(),
            )
        }
    }
