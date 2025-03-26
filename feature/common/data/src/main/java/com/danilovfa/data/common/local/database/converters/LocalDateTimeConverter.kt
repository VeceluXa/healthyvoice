package com.danilovfa.data.common.local.database.converters

import androidx.room.TypeConverter
import com.danilovfa.common.core.domain.time.utcFromEpochMilliseconds
import com.danilovfa.common.core.domain.time.utcToEpochMilliseconds
import kotlinx.datetime.LocalDateTime

internal class LocalDateTimeConverter {
    @TypeConverter
    fun entityToDatabase(localDateTime: LocalDateTime): Long {
        return localDateTime.utcToEpochMilliseconds()
    }

    @TypeConverter
    fun databaseToEntity(localDateTime: Long): LocalDateTime {
        return LocalDateTime.utcFromEpochMilliseconds(localDateTime)
    }
}