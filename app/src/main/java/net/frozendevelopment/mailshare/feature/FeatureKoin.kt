package net.frozendevelopment.mailshare.feature

import android.app.Application
import net.frozendevelopment.mailshare.data.sqldelight.CategoryQueries
import net.frozendevelopment.mailshare.data.sqldelight.LetterQueries
import net.frozendevelopment.mailshare.feature.category.form.CategoryFormMode
import net.frozendevelopment.mailshare.feature.category.form.CategoryFormViewModel
import net.frozendevelopment.mailshare.feature.category.manage.ManageCategoryViewModel
import net.frozendevelopment.mailshare.feature.mail.detail.LetterDetailViewModel
import net.frozendevelopment.mailshare.feature.mail.list.LetterListViewModel
import net.frozendevelopment.mailshare.feature.mail.scan.ScanViewModel
import net.frozendevelopment.mailshare.usecase.CreateLetterUseCase
import net.frozendevelopment.mailshare.usecase.LetterWithDetailsUseCase
import net.frozendevelopment.mailshare.usecase.SearchLettersUseCase
import net.frozendevelopment.mailshare.usecase.UpsertCategoryUseCase
import net.frozendevelopment.mailshare.util.TextExtractor
import net.frozendevelopment.mailshare.util.TextExtractorType
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Module

@Module
class FeatureKoin {
    @KoinViewModel
    fun letterListViewModel(
        letterQueries: LetterQueries,
        searchUseCase: SearchLettersUseCase,
        categoryQueries: CategoryQueries,
    ) = LetterListViewModel(letterQueries, searchUseCase, categoryQueries)

    @KoinViewModel
    fun scanViewModel(
        textExtractor: TextExtractorType,
        createLetter: CreateLetterUseCase,
        categoryQueries: CategoryQueries,
    ) = ScanViewModel(textExtractor, createLetter, categoryQueries)

    @KoinViewModel
    fun categoryFormViewModel(
        @InjectedParam mode: CategoryFormMode,
        upsertCategoryUseCase: UpsertCategoryUseCase,
        categoryQueries: CategoryQueries,
    ) = CategoryFormViewModel(mode, upsertCategoryUseCase, categoryQueries)

    @KoinViewModel
    fun manageCategoryViewModel(
        categoryQueries: CategoryQueries
    ) = ManageCategoryViewModel(categoryQueries)

    @KoinViewModel
    fun letterDetailViewModel(
        letterWithDetailsUseCase: LetterWithDetailsUseCase
    ) = LetterDetailViewModel(letterWithDetailsUseCase)
}
