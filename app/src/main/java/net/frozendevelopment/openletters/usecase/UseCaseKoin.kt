package net.frozendevelopment.openletters.usecase

import net.frozendevelopment.openletters.feature.reminder.notification.ReminderNotification
import net.frozendevelopment.openletters.feature.reminder.notification.ReminderNotificationType
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
// import org.koin.core.annotation.Factory
// import org.koin.core.annotation.Module

// // @Module
// class UseCaseKoin {
//    // @Factory
//    fun createLetterUseCase(
//        documentManager: DocumentManagerType,
//        database: OpenLettersDB,
//    ): UpsertLetterUseCase = UpsertLetterUseCase(documentManager, database)
//
//    // @Factory
//    fun metaLetterUseCase(letterQueries: LetterQueries): LetterCellUseCase = LetterCellUseCase(queries = letterQueries)
//
//    // @Factory
//    fun upsertCategoryUseCase(categoryQueries: CategoryQueries) = UpsertCategoryUseCase(categoryQueries)
//
//    // @Factory
//    fun letterWithDetailsUseCase(
//        documentManager: DocumentManagerType,
//        database: OpenLettersDB,
//    ) = LetterWithDetailsUseCase(documentManager, database)
//
//    // @Factory
//    fun searchLettersUseCase(letterQueries: LetterQueries) = SearchLettersUseCase(letterQueries)
//
//    // @Factory
//    fun reminderNotification(context: Context): ReminderNotificationType = ReminderNotification(context)
//
//    // @Factory
//    fun createReminderUseCase(
//        reminderQueries: ReminderQueries,
//        reminderNotification: ReminderNotificationType,
//    ) = UpsertReminderUseCase(reminderQueries, reminderNotification)
//
//    // @Factory
//    fun reminderWithDetailsUseCase(reminderQueries: ReminderQueries) = ReminderWithDetailsUseCase(reminderQueries)
//
//    // @Factory
//    fun acknowledgeReminderUseCase(
//        reminderQueries: ReminderQueries,
//        reminderNotification: ReminderNotificationType,
//    ) = AcknowledgeReminderUseCase(reminderQueries, reminderNotification)
//
//    // @Factory
//    fun deleteReminderUseCase(
//        reminderQueries: ReminderQueries,
//        reminderNotification: ReminderNotificationType,
//    ) = DeleteReminderUseCase(reminderQueries, reminderNotification)
//
//    // @Factory
//    fun deleteLetterUseCase(
//        letterQueries: LetterQueries,
//        documentQueries: DocumentQueries,
//        documentManager: DocumentManagerType,
//    ) = DeleteLetterUseCase(
//        letterQueries = letterQueries,
//        documentQueries = documentQueries,
//        documentManager = documentManager,
//    )
//
//    // @Factory
//    fun saveCategoryOrderUseCase(categoryQueries: CategoryQueries) = SaveCategoryOrderUseCase(categoryQueries)
// }

val useCaseKoinModule =
    module {
        factory { UpsertLetterUseCase(get(), get()) }
        factory { LetterCellUseCase(get()) }
        factory { UpsertCategoryUseCase(get()) }
        factory { LetterWithDetailsUseCase(get(), get()) }
        factory { SearchLettersUseCase(get()) }
        factory<ReminderNotificationType> { ReminderNotification(androidContext()) }
        factory { UpsertReminderUseCase(get(), get()) }
        factory { ReminderWithDetailsUseCase(get()) }
        factory { AcknowledgeReminderUseCase(get(), get()) }
        factory { DeleteReminderUseCase(get(), get()) }
        factory { DeleteLetterUseCase(get(), get(), get()) }
        factory { SaveCategoryOrderUseCase(get()) }
    }
