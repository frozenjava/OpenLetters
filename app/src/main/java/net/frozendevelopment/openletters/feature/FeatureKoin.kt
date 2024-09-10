package net.frozendevelopment.openletters.feature

import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.feature.category.form.CategoryFormMode
import net.frozendevelopment.openletters.feature.category.form.CategoryFormViewModel
import net.frozendevelopment.openletters.feature.category.manage.ManageCategoryViewModel
import net.frozendevelopment.openletters.feature.letter.detail.LetterDetailViewModel
import net.frozendevelopment.openletters.feature.letter.list.LetterListViewModel
import net.frozendevelopment.openletters.feature.letter.scan.ScanViewModel
import net.frozendevelopment.openletters.usecase.CreateLetterUseCase
import net.frozendevelopment.openletters.usecase.LetterWithDetailsUseCase
import net.frozendevelopment.openletters.usecase.SearchLettersUseCase
import net.frozendevelopment.openletters.usecase.UpsertCategoryUseCase
import net.frozendevelopment.openletters.util.TextExtractorType
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Module

@Module
class FeatureKoin {
    @KoinViewModel
    fun letterListViewModel(
        letterQueries: LetterQueries,
        searchUseCase: SearchLettersUseCase,
        categoryQueries: net.frozendevelopment.openletters.data.sqldelight.CategoryQueries,
    ) = LetterListViewModel(letterQueries, searchUseCase, categoryQueries)

    @KoinViewModel
    fun scanViewModel(
        textExtractor: TextExtractorType,
        createLetter: CreateLetterUseCase,
        categoryQueries: net.frozendevelopment.openletters.data.sqldelight.CategoryQueries,
    ) = ScanViewModel(textExtractor, createLetter, categoryQueries)

    @KoinViewModel
    fun categoryFormViewModel(
        @InjectedParam mode: CategoryFormMode,
        upsertCategoryUseCase: UpsertCategoryUseCase,
        categoryQueries: net.frozendevelopment.openletters.data.sqldelight.CategoryQueries,
    ) = CategoryFormViewModel(mode, upsertCategoryUseCase, categoryQueries)

    @KoinViewModel
    fun manageCategoryViewModel(
        categoryQueries: net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
    ) = ManageCategoryViewModel(categoryQueries)

    @KoinViewModel
    fun letterDetailViewModel(
        letterWithDetailsUseCase: LetterWithDetailsUseCase
    ) = LetterDetailViewModel(letterWithDetailsUseCase)
}
