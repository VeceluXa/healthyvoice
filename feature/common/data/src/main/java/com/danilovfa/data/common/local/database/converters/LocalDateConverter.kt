package com.danilovfa.data.common.local.database.converters

import androidx.room.TypeConverter
import com.danilovfa.common.core.domain.time.utcFromEpochMilliseconds
import com.danilovfa.common.core.domain.time.utcToEpochMilliseconds
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

internal class LocalDateConverter {
    @TypeConverter
    fun entityToDatabase(localDate: LocalDate): Int {
        return localDate.toEpochDays()
    }

    @TypeConverter
    fun databaseToEntity(epochDays: Int): LocalDate {
        return LocalDate.fromEpochDays(epochDays)
    }
}