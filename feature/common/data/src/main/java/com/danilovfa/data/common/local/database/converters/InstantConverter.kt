package com.danilovfa.data.common.local.database.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

internal class InstantConverter {
    @TypeConverter
    fun entityToDatabase(instant: Instant): Long {
        return instant.toEpochMilliseconds()
    }

    @TypeConverter
    fun databaseToEntity(epochMilliseconds: Long): Instant {
        return Instant.fromEpochMilliseconds(epochMilliseconds)
    }
}