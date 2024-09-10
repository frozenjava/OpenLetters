package net.frozendevelopment.openletters.migration

import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.data.sqldelight.OpenLettersDB
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import java.time.Instant

class TestDataMigration(
    private val letterQueries: LetterQueries,
) : AppMigration {

    override val migrationKey: String
        get() = "test_data"

    override fun invoke() {
        val addresses = listOf(
            "Steve Martin 123 Main St, Anytown USA",
            "Bob Smith 456 Elm St, Anytown USA",
            "Jane Doe 789 Oak St, Anytown USA",
            "Alice Johnson 321 Maple St, Anytown USA",
            "David Lee 654 Pine St, Anytown USA",
        )

        // create a list of messages that sound like something you'd find in a letter
        val message = "We've been trying to reach you about your car"

        letterQueries.transaction {
            for (address in addresses) {
                letterQueries.upsertLetter(
                    id = LetterId.random(),
                    sender = address,
                    recipient = address,
                    body = message,
                    created = Instant.now().epochSecond,
                    lastModified = Instant.now().epochSecond,
                )
            }
        }
    }
}