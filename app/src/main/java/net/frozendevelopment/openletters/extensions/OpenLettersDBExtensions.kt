package net.frozendevelopment.openletters.extensions

import app.cash.sqldelight.db.SqlDriver
import net.frozendevelopment.openletters.data.sqldelight.OpenLettersDB
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.migrations.Document
import net.frozendevelopment.openletters.data.sqldelight.migrations.Letter
import net.frozendevelopment.openletters.data.sqldelight.migrations.LetterToCategory
import net.frozendevelopment.openletters.data.sqldelight.migrations.LetterToReminder
import net.frozendevelopment.openletters.data.sqldelight.migrations.Reminder
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.ColorAdapter
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.LocalDateTimeAdapter
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId

operator fun OpenLettersDB.Companion.invoke(driver: SqlDriver): OpenLettersDB {
    driver.execute(null, "PRAGMA foreign_keys = ON;", 0)
    return OpenLettersDB(
        driver = driver,
        letterAdapter =
            Letter.Adapter(
                idAdapter = LetterId.adapter,
                createdAdapter = LocalDateTimeAdapter,
                lastModifiedAdapter = LocalDateTimeAdapter,
            ),
        documentAdapter =
            Document.Adapter(
                idAdapter = DocumentId.adapter,
                letterIdAdapter = LetterId.adapter,
            ),
        categoryAdapter =
            Category.Adapter(
                idAdapter = CategoryId.adapter,
                colorAdapter = ColorAdapter,
                createdAdapter = LocalDateTimeAdapter,
                lastModifiedAdapter = LocalDateTimeAdapter,
            ),
        letterToCategoryAdapter =
            LetterToCategory.Adapter(
                letterIdAdapter = LetterId.adapter,
                categoryIdAdapter = CategoryId.adapter,
            ),
        reminderAdapter =
            Reminder.Adapter(
                idAdapter = ReminderId.adapter,
                createdAdapter = LocalDateTimeAdapter,
                lastModifiedAdapter = LocalDateTimeAdapter,
                scheduledForAdapter = LocalDateTimeAdapter,
            ),
        letterToReminderAdapter =
            LetterToReminder.Adapter(
                letterIdAdapter = LetterId.adapter,
                reminderIdAdapter = ReminderId.adapter,
            ),
    )
}
