package net.frozendevelopment.mailshare.usecase

import android.app.Application
import net.frozendevelopment.mailshare.data.sqldelight.MailShareDB
import net.frozendevelopment.mailshare.util.TextExtractorType
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class UseCaseKoin {

    @Factory
    fun createLetterUseCase(
        textExtractor: TextExtractorType,
        database: MailShareDB
    ): CreateLetterUseCase {
        return CreateLetterUseCase(
            textExtractor,
            database
        )
    }

}
