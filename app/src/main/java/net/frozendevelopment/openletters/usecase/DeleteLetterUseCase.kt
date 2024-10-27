package net.frozendevelopment.openletters.usecase

import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId

class DeleteLetterUseCase(
    private val letterQueries: LetterQueries,
) {
    operator fun invoke(id: LetterId) {
        letterQueries.deleteLetter(id)
    }
}
