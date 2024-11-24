package net.frozendevelopment.openletters.util

import android.content.Context

interface VibrationEngineType {
    fun short()
}

class VibrationEngine(
    private val context: Context,
) : VibrationEngineType {
    override fun short() {
        TODO("Not yet implemented")
    }
}
