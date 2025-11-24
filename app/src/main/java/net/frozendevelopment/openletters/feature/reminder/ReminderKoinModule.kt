package net.frozendevelopment.openletters.feature.reminder

import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailViewModel
import net.frozendevelopment.openletters.feature.reminder.detail.reminderDetailNavigation
import net.frozendevelopment.openletters.feature.reminder.form.ReminderFormViewModel
import net.frozendevelopment.openletters.feature.reminder.form.reminderFormNavigation
import net.frozendevelopment.openletters.feature.reminder.list.ReminderListViewModel
import net.frozendevelopment.openletters.feature.reminder.list.reminderListNavigation
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
// import org.koin.android.annotation.KoinViewModel
// import org.koin.core.annotation.InjectedParam
// import org.koin.core.annotation.Module

// @Module
// class ReminderKoinModule {
//    // @KoinViewModel
//    fun reminderListViewModel(
//        application: Application,
//        reminderQueries: ReminderQueries,
//        deleteReminder: DeleteReminderUseCase,
//    ) = ReminderListViewModel(
//        application = application,
//        reminderQueries = reminderQueries,
//        deleteReminder = deleteReminder,
//    )
//
//    // @KoinViewModel
//    fun reminderFormViewModel(
//        @InjectedParam reminderToEdit: ReminderId?,
//        @InjectedParam preselectedLetters: List<LetterId>,
//        application: Application,
//        searchLettersUseCase: SearchLettersUseCase,
//        upsertReminderUseCase: UpsertReminderUseCase,
//        reminderQueries: ReminderQueries,
//    ) = ReminderFormViewModel(
//        reminderToEdit = reminderToEdit,
//        application = application,
//        searchLetters = searchLettersUseCase,
//        createReminder = upsertReminderUseCase,
//        reminderQueries = reminderQueries,
//        preselectedLetters = preselectedLetters,
//    )
//
//    // @KoinViewModel
//    fun reminderDetailViewModel(
//        @InjectedParam reminderId: ReminderId,
//        application: Application,
//        reminderWithDetailsUseCase: ReminderWithDetailsUseCase,
//        acknowledgeReminderUseCase: AcknowledgeReminderUseCase,
//    ) = ReminderDetailViewModel(
//        reminderId = reminderId,
//        application = application,
//        reminderWithDetails = reminderWithDetailsUseCase,
//        acknowledgeReminder = acknowledgeReminderUseCase,
//    )
//
//    companion object {
//        val navigationModule = module {
//            reminderDetailNavigation()
//            reminderFormNavigation()
//            reminderListNavigation()
//        }
//    }
// }

val reminderKoinModule =
    module {
        reminderDetailNavigation()
        reminderFormNavigation()
        reminderListNavigation()
        viewModel {
            ReminderListViewModel(
                application = get(),
                reminderQueries = get(),
                deleteReminder = get(),
            )
        }

        viewModel { (reminderToEdit: ReminderId?, preselectedLetters: List<LetterId>) ->
            ReminderFormViewModel(
                reminderToEdit = reminderToEdit,
                application = get(),
                searchLetters = get(),
                createReminder = get(),
                reminderQueries = get(),
                preselectedLetters = preselectedLetters,
            )
        }

        viewModel { (reminderId: ReminderId) ->
            ReminderDetailViewModel(
                reminderId = reminderId,
                application = get(),
                reminderWithDetails = get(),
                acknowledgeReminder = get(),
            )
        }
    }
