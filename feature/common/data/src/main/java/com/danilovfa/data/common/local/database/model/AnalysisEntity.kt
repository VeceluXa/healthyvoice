package com.danilovfa.data.common.local.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.danilovfa.domain.common.model.Analysis
import kotlinx.datetime.Instant

@Entity(
    tableName = "analysis",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecordingEntity::class,
            parentColumns = ["id"],
            childColumns = ["recordingId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["patientId"]),
        Index(value = ["recordingId"])
    ]
)
data class AnalysisEntity(
    @PrimaryKey(autoGenerate = false)
    val recordingId: Long,
    val patientId: Long,
    val timestamp: Instant,
    val j1: Float,
    val j3: Float,
    val j5: Float,
    val s1: Float,
    val s3: Float,
    val s5: Float,
    val s11: Float,
    val f0: Float,
    val f0sd: Float
)

fun AnalysisEntity.toDomain() = Analysis(
    patientId = patientId,
    recordingId = recordingId,
    timestamp = timestamp,
    j1 = j1,
    j3 = j3,
    j5 = j5,
    s1 = s1,
    s3 = s3,
    s5 = s5,
    s11 = s11,
    f0 = f0,
    f0sd = f0sd
)

fun Analysis.toEntity() = AnalysisEntity(
    patientId = patientId,
    recordingId = recordingId,
    timestamp = timestamp,
    j1 = j1,
    j3 = j3,
    j5 = j5,
    s1 = s1,
    s3 = s3,
    s5 = s5,
    s11 = s11,
    f0 = f0,
    f0sd = f0sd
)