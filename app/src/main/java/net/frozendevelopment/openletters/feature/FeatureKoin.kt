package net.frozendevelopment.openletters.feature

import android.app.Application
import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.feature.category.form.CategoryFormMode
import net.frozendevelopment.openletters.feature.category.form.CategoryFormViewModel
import net.frozendevelopment.openletters.feature.category.manage.ManageCategoryViewModel
import net.frozendevelopment.openletters.feature.letter.detail.LetterDetailViewModel
import net.frozendevelopment.openletters.feature.letter.list.LetterListViewModel
import net.frozendevelopment.openletters.feature.letter.peek.LetterPeekViewModel
import net.frozendevelopment.openletters.feature.letter.scan.ScanViewModel
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailViewModel
import net.frozendevelopment.openletters.feature.reminder.form.ReminderFormViewModel
import net.frozendevelopment.openletters.feature.reminder.list.ReminderListViewModel
import net.frozendevelopment.openletters.feature.settings.SettingsViewModel
import net.frozendevelopment.openletters.usecase.AcknowledgeReminderUseCase
import net.frozendevelopment.openletters.usecase.DeleteLetterUseCase
import net.frozendevelopment.openletters.usecase.DeleteReminderUseCase
import net.frozendevelopment.openletters.usecase.LetterWithDetailsUseCase
import net.frozendevelopment.openletters.usecase.ReminderWithDetailsUseCase
import net.frozendevelopment.openletters.usecase.SaveCategoryOrderUseCase
import net.frozendevelopment.openletters.usecase.SearchLettersUseCase
import net.frozendevelopment.openletters.usecase.UpsertCategoryUseCase
import net.frozendevelopment.openletters.usecase.UpsertLetterUseCase
import net.frozendevelopment.openletters.usecase.UpsertReminderUseCase
import net.frozendevelopment.openletters.util.TextExtractorType
import net.frozendevelopment.openletters.util.ThemeManagerType
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
        deleteLetterUseCase: DeleteLetterUseCase,
    ) = LetterListViewModel(
        reminderQueries = reminderQueries,
        letterQueries = letterQueries,
        searchUseCase = searchUseCase,
        categoryQueries = categoryQueries,
        deleteLetter = deleteLetterUseCase,
    )

    @KoinViewModel
    fun scanViewModel(
        @InjectedParam letterToEdit: LetterId?,
        letterQueries: LetterQueries,
        textExtractor: TextExtractorType,
        createLetter: UpsertLetterUseCase,
        categoryQueries: CategoryQueries,
        letterWithDetailsUseCase: LetterWithDetailsUseCase,
    ) = ScanViewModel(
        letterToEdit = letterToEdit,
        letterQueries = letterQueries,
        textExtractor = textExtractor,
        createLetter = createLetter,
        categoryQueries = categoryQueries,
        letterWithDetails = letterWithDetailsUseCase,
    )

    @KoinViewModel
    fun categoryFormViewModel(
        @InjectedParam mode: CategoryFormMode,
        upsertCategoryUseCase: UpsertCategoryUseCase,
        categoryQueries: CategoryQueries,
    ) = CategoryFormViewModel(
        mode = mode,
        upsertCategoryUseCase = upsertCategoryUseCase,
        categoryQueries = categoryQueries,
    )

    @KoinViewModel
    fun manageCategoryViewModel(
        saveCategoryOrderUseCase: SaveCategoryOrderUseCase,
        categoryQueries: CategoryQueries,
    ) = ManageCategoryViewModel(saveCategoryOrderUseCase, categoryQueries)

    @KoinViewModel
    fun letterDetailViewModel(
        @InjectedParam letterId: LetterId,
        letterWithDetailsUseCase: LetterWithDetailsUseCase,
    ) = LetterDetailViewModel(letterId, letterWithDetailsUseCase)

    @KoinViewModel
    fun letterPeekViewModel(
        @InjectedParam letterId: LetterId,
        letterWithDetails: LetterWithDetailsUseCase,
    ) = LetterPeekViewModel(letterId, letterWithDetails)

    @KoinViewModel
    fun reminderListViewModel(
        application: Application,
        reminderQueries: ReminderQueries,
        deleteReminder: DeleteReminderUseCase,
    ) = ReminderListViewModel(
        application = application,
        reminderQueries = reminderQueries,
        deleteReminder = deleteReminder,
    )

    @KoinViewModel
    fun reminderFormViewModel(
        @InjectedParam reminderToEdit: ReminderId?,
        @InjectedParam preselectedLetters: List<LetterId>,
        application: Application,
        searchLettersUseCase: SearchLettersUseCase,
        upsertReminderUseCase: UpsertReminderUseCase,
        reminderQueries: ReminderQueries,
    ) = ReminderFormViewModel(
        reminderToEdit = reminderToEdit,
        application = application,
        searchLetters = searchLettersUseCase,
        createReminder = upsertReminderUseCase,
        reminderQueries = reminderQueries,
        preselectedLetters = preselectedLetters,
    )

    @KoinViewModel
    fun reminderDetailViewModel(
        @InjectedParam reminderId: ReminderId,
        application: Application,
        reminderWithDetailsUseCase: ReminderWithDetailsUseCase,
        acknowledgeReminderUseCase: AcknowledgeReminderUseCase,
    ) = ReminderDetailViewModel(
        reminderId = reminderId,
        application = application,
        reminderWithDetails = reminderWithDetailsUseCase,
        acknowledgeReminder = acknowledgeReminderUseCase,
    )

    @KoinViewModel
    fun settingsViewModel(
        application: Application,
        themeManager: ThemeManagerType,
    ) = SettingsViewModel(
        appVersion = application.packageManager.getPackageInfo(application.packageName, 0).versionName ?: "0.0.0",
        themeManager = themeManager,
    )
}
