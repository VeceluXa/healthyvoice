package com.danilovfa.data.common.local.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.danilovfa.domain.common.model.PatientDetails

data class PatientAnalysisEntity(
    @Embedded
    val patient: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patientId"
    )
    val analyzes: List<AnalysisEntity>
)

fun PatientAnalysisEntity.toDomain() = PatientDetails(
    patient = patient.toDomain(),
    analyzes = analyzes.map { it.toDomain() }
)
