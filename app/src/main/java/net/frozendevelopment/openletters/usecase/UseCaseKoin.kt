package net.frozendevelopment.openletters.usecase

import android.content.Context
import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import net.frozendevelopment.openletters.data.sqldelight.DocumentQueries
import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.data.sqldelight.OpenLettersDB
import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.feature.reminder.notification.ReminderNotification
import net.frozendevelopment.openletters.feature.reminder.notification.ReminderNotificationType
import net.frozendevelopment.openletters.util.DocumentManagerType
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class UseCaseKoin {
    @Factory
    fun createLetterUseCase(
        documentManager: DocumentManagerType,
        database: OpenLettersDB,
    ): UpsertLetterUseCase = UpsertLetterUseCase(documentManager, database)

    @Factory
    fun metaLetterUseCase(letterQueries: LetterQueries): LetterCellUseCase = LetterCellUseCase(queries = letterQueries)

    @Factory
    fun upsertCategoryUseCase(categoryQueries: CategoryQueries) = UpsertCategoryUseCase(categoryQueries)

    @Factory
    fun letterWithDetailsUseCase(
        documentManager: DocumentManagerType,
        database: OpenLettersDB,
    ) = LetterWithDetailsUseCase(documentManager, database)

    @Factory
    fun searchLettersUseCase(letterQueries: LetterQueries) = SearchLettersUseCase(letterQueries)

    @Factory
    fun reminderNotification(context: Context): ReminderNotificationType = ReminderNotification(context)

    @Factory
    fun createReminderUseCase(
        reminderQueries: ReminderQueries,
        reminderNotification: ReminderNotificationType,
    ) = UpsertReminderUseCase(reminderQueries, reminderNotification)

    @Factory
    fun reminderWithDetailsUseCase(reminderQueries: ReminderQueries) = ReminderWithDetailsUseCase(reminderQueries)

    @Factory
    fun acknowledgeReminderUseCase(
        reminderQueries: ReminderQueries,
        reminderNotification: ReminderNotificationType,
    ) = AcknowledgeReminderUseCase(reminderQueries, reminderNotification)

    @Factory
    fun deleteReminderUseCase(
        reminderQueries: ReminderQueries,
        reminderNotification: ReminderNotificationType,
    ) = DeleteReminderUseCase(reminderQueries, reminderNotification)

    @Factory
    fun deleteLetterUseCase(
        letterQueries: LetterQueries,
        documentQueries: DocumentQueries,
        documentManager: DocumentManagerType,
    ) = DeleteLetterUseCase(
        letterQueries = letterQueries,
        documentQueries = documentQueries,
        documentManager = documentManager,
    )

    @Factory
    fun saveCategoryOrderUseCase(categoryQueries: CategoryQueries) = SaveCategoryOrderUseCase(categoryQueries)
}
