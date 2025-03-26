package com.danilovfa.data.common.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.danilovfa.data.common.local.database.converters.LocalDateConverter
import com.danilovfa.data.common.local.database.converters.LocalDateTimeConverter
import com.danilovfa.data.common.local.database.dao.AnalysisDao
import com.danilovfa.data.common.local.database.dao.PatientDao
import com.danilovfa.data.common.local.database.dao.PatientDetailsDao
import com.danilovfa.data.common.local.database.model.AnalysisEntity
import com.danilovfa.data.common.local.database.model.PatientEntity

@Database(
    entities = [
        PatientEntity::class,
        AnalysisEntity::class
    ],
    exportSchema = false,
    version = 1
)
@TypeConverters(LocalDateConverter::class, LocalDateTimeConverter::class)
internal abstract class Database : RoomDatabase() {
    abstract val patientDao: PatientDao
    abstract val analysisDao: AnalysisDao
    abstract val patientDetailsDao: PatientDetailsDao
}