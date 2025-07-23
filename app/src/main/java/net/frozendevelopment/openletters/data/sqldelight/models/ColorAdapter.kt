package net.frozendevelopment.openletters.data.sqldelight.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import app.cash.sqldelight.ColumnAdapter

object ColorAdapter : ColumnAdapter<Color, Long> {
    override fun decode(databaseValue: Long): Color = Color(databaseValue).copy(1f)

    override fun encode(value: Color): Long = value.toArgb().toLong()
}
