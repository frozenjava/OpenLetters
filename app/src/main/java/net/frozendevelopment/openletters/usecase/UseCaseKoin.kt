package net.frozendevelopment.openletters.usecase

import android.content.Context
import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.data.sqldelight.OpenLettersDB
import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.feature.reminder.notification.ReminderNotification
import net.frozendevelopment.openletters.util.DocumentManagerType
import net.frozendevelopment.openletters.util.TextExtractorType
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class UseCaseKoin {

    @Factory
    fun createLetterUseCase(
        documentManager: DocumentManagerType,
        textExtractor: TextExtractorType,
        database: OpenLettersDB
    ): CreateLetterUseCase {
        return CreateLetterUseCase(
            documentManager,
            textExtractor,
            database
        )
    }

    @Factory
    fun metaLetterUseCase(
        letterQueries: LetterQueries
    ): MetaLetterUseCase {
        return MetaLetterUseCase(queries = letterQueries)
    }

    @Factory
    fun upsertCategoryUseCase(
        categoryQueries: net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
    ) = UpsertCategoryUseCase(categoryQueries)

    @Factory
    fun letterWithDetailsUseCase(
        documentManager: DocumentManagerType,
        database: OpenLettersDB
    ) = LetterWithDetailsUseCase(documentManager, database)

    @Factory
    fun searchLettersUseCase(
        letterQueries: LetterQueries
    ) = SearchLettersUseCase(letterQueries)

    @Factory
    fun reminderNotification(context: Context) = ReminderNotification(context)

    @Factory
    fun createReminderUseCase(
        reminderQueries: ReminderQueries,
        reminderNotification: ReminderNotification
    ) = CreateReminderUseCase(reminderQueries, reminderNotification)

    @Factory
    fun reminderWithDetailsUseCase(
        reminderQueries: ReminderQueries
    ) = ReminderWithDetailsUseCase(reminderQueries)

    @Factory
    fun acknowledgeReminderUseCase(
        reminderQueries: ReminderQueries,
        reminderNotification: ReminderNotification
    ) = AcknowledgeReminderUseCase(reminderQueries, reminderNotification)
}
