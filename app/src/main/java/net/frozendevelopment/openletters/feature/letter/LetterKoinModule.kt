package net.frozendevelopment.openletters.feature.letter

import androidx.compose.runtime.getValue
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.feature.letter.detail.LetterDetailViewModel
import net.frozendevelopment.openletters.feature.letter.detail.letterDetailNavigation
import net.frozendevelopment.openletters.feature.letter.image.imageViewNavigation
import net.frozendevelopment.openletters.feature.letter.list.LetterListViewModel
import net.frozendevelopment.openletters.feature.letter.list.letterListNavigation
import net.frozendevelopment.openletters.feature.letter.peek.LetterPeekViewModel
import net.frozendevelopment.openletters.feature.letter.scan.ScanViewModel
import net.frozendevelopment.openletters.feature.letter.scan.scanLetterNavigation
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
// import org.koin.android.annotation.KoinViewModel
// import org.koin.core.annotation.Module

// @Module
// class LetterKoinModule {
//    // @KoinViewModel
//    fun letterListViewModel(
//        reminderQueries: ReminderQueries,
//        letterQueries: LetterQueries,
//        searchUseCase: SearchLettersUseCase,
//        categoryQueries: CategoryQueries,
//        deleteLetterUseCase: DeleteLetterUseCase,
//    ) = LetterListViewModel(
//        reminderQueries = reminderQueries,
//        letterQueries = letterQueries,
//        searchUseCase = searchUseCase,
//        categoryQueries = categoryQueries,
//        deleteLetter = deleteLetterUseCase,
//    )
//
//    // @KoinViewModel
//    fun scanViewModel(
//        @InjectedParam letterToEdit: LetterId?,
//        letterQueries: LetterQueries,
//        textExtractor: TextExtractorType,
//        createLetter: UpsertLetterUseCase,
//        categoryQueries: CategoryQueries,
//        letterWithDetailsUseCase: LetterWithDetailsUseCase,
//    ) = ScanViewModel(
//        letterToEdit = letterToEdit,
//        letterQueries = letterQueries,
//        textExtractor = textExtractor,
//        createLetter = createLetter,
//        categoryQueries = categoryQueries,
//        letterWithDetails = letterWithDetailsUseCase,
//    )
//
//    // @KoinViewModel
//    fun letterDetailViewModel(
//        @InjectedParam letterId: LetterId,
//        letterWithDetailsUseCase: LetterWithDetailsUseCase,
//    ) = LetterDetailViewModel(letterId, letterWithDetailsUseCase)
//
//    // @KoinViewModel
//    fun letterPeekViewModel(
//        @InjectedParam letterId: LetterId,
//        letterWithDetails: LetterWithDetailsUseCase,
//    ) = LetterPeekViewModel(letterId, letterWithDetails)
//
//    // Note: reminderListViewModel is provided in ReminderKoinModule DSL to avoid duplication
//
//    companion object {
//        @OptIn(KoinExperimentalAPI::class)
//        val navigationModule = module {
//            letterDetailNavigation()
//            imageViewNavigation()
//            letterListNavigation()
//            scanLetterNavigation()
//        }
//    }
// }

val letterKoinModule =
    module {
        letterDetailNavigation()
        imageViewNavigation()
        letterListNavigation()
        scanLetterNavigation()
        viewModel {
            LetterListViewModel(
                reminderQueries = get(),
                letterQueries = get(),
                searchUseCase = get(),
                categoryQueries = get(),
                deleteLetter = get(),
            )
        }

        viewModel { (letterToEdit: LetterId?) ->
            ScanViewModel(
                letterToEdit = letterToEdit,
                letterQueries = get(),
                textExtractor = get(),
                createLetter = get(),
                categoryQueries = get(),
                letterWithDetails = get(),
            )
        }

        viewModel { (letterId: LetterId) ->
            LetterDetailViewModel(letterId, get())
        }

        viewModel { (letterId: LetterId) ->
            LetterPeekViewModel(letterId, get())
        }
    }
