package com.danilovfa.data.common.local.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class ExportEntity(
    @Embedded val patient: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patientId",
        entity = RecordingEntity::class
    )
    val analysis: List<RecordingAnalysisEntity>,
)
