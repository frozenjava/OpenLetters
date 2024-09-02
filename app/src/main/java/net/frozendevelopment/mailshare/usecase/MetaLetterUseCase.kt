package net.frozendevelopment.mailshare.usecase

import net.frozendevelopment.mailshare.data.sqldelight.LetterInfo
import net.frozendevelopment.mailshare.data.sqldelight.LetterQueries
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId

class MetaLetterUseCase(
    private val queries: LetterQueries
) {
    fun load(id: LetterId): LetterInfo? {
        return queries.letterInfo(id)
            .executeAsOneOrNull()
    }
}
