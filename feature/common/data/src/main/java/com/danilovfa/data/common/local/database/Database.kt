package com.danilovfa.data.common.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.danilovfa.data.common.local.database.converters.InstantConverter
import com.danilovfa.data.common.local.database.converters.LocalDateConverter
import com.danilovfa.data.common.local.database.converters.LocalDateTimeConverter
import com.danilovfa.data.common.local.database.dao.AnalysisDao
import com.danilovfa.data.common.local.database.dao.ExportDao
import com.danilovfa.data.common.local.database.dao.PatientDao
import com.danilovfa.data.common.local.database.dao.RecordingAnalysisDao
import com.danilovfa.data.common.local.database.dao.RecordingDao
import com.danilovfa.data.common.local.database.model.AnalysisEntity
import com.danilovfa.data.common.local.database.model.PatientEntity
import com.danilovfa.data.common.local.database.model.RecordingEntity

@Database(
    entities = [
        PatientEntity::class,
        AnalysisEntity::class,
        RecordingEntity::class
    ],
    exportSchema = false,
    version = 1
)
@TypeConverters(LocalDateConverter::class, LocalDateTimeConverter::class, InstantConverter::class)
internal abstract class Database : RoomDatabase() {
    abstract val patientDao: PatientDao
    abstract val analysisDao: AnalysisDao
    abstract val recordingDao: RecordingDao
    abstract val recordingAnalysisDao: RecordingAnalysisDao
    abstract val exportDao: ExportDao
}