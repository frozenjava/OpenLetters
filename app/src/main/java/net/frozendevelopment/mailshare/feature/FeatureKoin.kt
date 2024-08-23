package net.frozendevelopment.mailshare.feature

import net.frozendevelopment.mailshare.data.sqldelight.LetterQueries
import net.frozendevelopment.mailshare.feature.list.LetterListViewModel
import net.frozendevelopment.mailshare.feature.scan.ScanViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class FeatureKoin {

    @KoinViewModel
    fun letterListViewModel(letterQueries: LetterQueries) = LetterListViewModel(letterQueries)

    @KoinViewModel
    fun scanViewModel() = ScanViewModel()

}