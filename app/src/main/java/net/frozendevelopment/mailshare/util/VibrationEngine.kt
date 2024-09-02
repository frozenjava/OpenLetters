package net.frozendevelopment.mailshare.util

import android.content.Context
import android.os.Build
import android.os.VibratorManager
import androidx.annotation.RequiresApi

interface VibrationEngineType {
    fun short()
}

class VibrationEngine(
    private val context: Context
): VibrationEngineType {

    override fun short() {
        TODO("Not yet implemented")
    }

}
