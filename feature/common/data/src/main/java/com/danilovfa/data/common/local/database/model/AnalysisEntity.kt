package com.danilovfa.data.common.local.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.danilovfa.domain.common.model.Analysis
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "analysis",
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = ["id"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(
        value = ["patientId"],
        unique = true
    )]
)
data class AnalysisEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val patientId: String,
    val filename: String,
    val timestamp: LocalDateTime,
    val cutStart: Int,
    val cutEnd: Int,
    val j0: Float,
    val j3: Float,
    val j5: Float,
    val s3: Float,
    val s5: Float,
    val s11: Float,
    val f0: Float,
    val f0sd: Float
)

fun AnalysisEntity.toDomain() = Analysis(
    id = id,
    patientId = patientId,
    filename = filename,
    timestamp = timestamp,
    cutStart = cutStart,
    cutEnd = cutEnd,
    j0 = j0,
    j3 = j3,
    j5 = j5,
    s3 = s3,
    s5 = s5,
    s11 = s11,
    f0 = f0,
    f0sd = f0sd
)

fun Analysis.toEntity() = AnalysisEntity(
    id = id,
    patientId = patientId,
    filename = filename,
    timestamp = timestamp,
    cutStart = cutStart,
    cutEnd = cutEnd,
    j0 = j0,
    j3 = j3,
    j5 = j5,
    s3 = s3,
    s5 = s5,
    s11 = s11,
    f0 = f0,
    f0sd = f0sd
)