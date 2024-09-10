package net.frozendevelopment.mailshare.util

import android.app.Application
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class UtilKoin {
    @Factory
    fun textExtractor(application: Application): TextExtractorType {
        return TextExtractor(application)
    }

    @Factory
    fun documentManager(application: Application): DocumentManagerType {
        return DocumentManager(application)
    }
}
