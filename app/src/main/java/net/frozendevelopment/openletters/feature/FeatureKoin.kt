package net.frozendevelopment.openletters.feature

import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.feature.category.form.CategoryFormMode
import net.frozendevelopment.openletters.feature.category.form.CategoryFormViewModel
import net.frozendevelopment.openletters.feature.category.manage.ManageCategoryViewModel
import net.frozendevelopment.openletters.feature.letter.detail.LetterDetailViewModel
import net.frozendevelopment.openletters.feature.letter.list.LetterListViewModel
import net.frozendevelopment.openletters.feature.letter.scan.ScanViewModel
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailViewModel
import net.frozendevelopment.openletters.feature.reminder.form.ReminderFormViewModel
import net.frozendevelopment.openletters.feature.reminder.list.ReminderListViewModel
import net.frozendevelopment.openletters.usecase.AcknowledgeReminderUseCase
import net.frozendevelopment.openletters.usecase.CreateLetterUseCase
import net.frozendevelopment.openletters.usecase.CreateReminderUseCase
import net.frozendevelopment.openletters.usecase.LetterWithDetailsUseCase
import net.frozendevelopment.openletters.usecase.ReminderWithDetailsUseCase
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
        reminderQueries: ReminderQueries,
        letterQueries: LetterQueries,
        searchUseCase: SearchLettersUseCase,
        categoryQueries: CategoryQueries,
    ) = LetterListViewModel(
        reminderQueries = reminderQueries,
        letterQueries = letterQueries,
        searchUseCase = searchUseCase,
        categoryQueries = categoryQueries
    )

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

    @KoinViewModel
    fun reminderListViewModel(
        reminderQueries: ReminderQueries
    ) = ReminderListViewModel(reminderQueries)

    @KoinViewModel
    fun reminderFormViewModel(
        @InjectedParam reminderToEdit: ReminderId,
        searchLettersUseCase: SearchLettersUseCase,
        createReminderUseCase: CreateReminderUseCase,
        reminderQueries: ReminderQueries,
    ) = ReminderFormViewModel(
        reminderToEdit = reminderToEdit,
        searchLetters = searchLettersUseCase,
        createReminder = createReminderUseCase,
        reminderQueries = reminderQueries,
    )

    @KoinViewModel
    fun reminderDetailViewModel(
        @InjectedParam reminderId: ReminderId,
        reminderWithDetailsUseCase: ReminderWithDetailsUseCase,
        acknowledgeReminderUseCase: AcknowledgeReminderUseCase
    ) = ReminderDetailViewModel(
        reminderId = reminderId,
        reminderWithDetails = reminderWithDetailsUseCase,
        acknowledgeReminder = acknowledgeReminderUseCase
    )
}
