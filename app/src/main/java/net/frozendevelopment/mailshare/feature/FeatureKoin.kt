package net.frozendevelopment.mailshare.feature

import android.app.Application
import net.frozendevelopment.mailshare.data.sqldelight.CategoryQueries
import net.frozendevelopment.mailshare.data.sqldelight.LetterQueries
import net.frozendevelopment.mailshare.feature.list.LetterListViewModel
import net.frozendevelopment.mailshare.feature.scan.ScanViewModel
import net.frozendevelopment.mailshare.usecase.CreateLetterUseCase
import net.frozendevelopment.mailshare.util.TextExtractor
import net.frozendevelopment.mailshare.util.TextExtractorType
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class FeatureKoin {
    @KoinViewModel
    fun letterListViewModel(
        letterQueries: LetterQueries,
        categoryQueries: CategoryQueries,
    ) = LetterListViewModel(letterQueries, categoryQueries)

    @KoinViewModel
    fun scanViewModel(
        textExtractor: TextExtractorType,
        createLetter: CreateLetterUseCase,
        categoryQueries: CategoryQueries,
    ) = ScanViewModel(textExtractor, createLetter, categoryQueries)
}
