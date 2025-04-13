package com.danilovfa.data.common.local.database.model

import android.content.Context
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.danilovfa.data.common.utils.RecordingUtils
import com.danilovfa.domain.common.model.Recording
import kotlinx.datetime.Instant
import java.io.File

@Entity(
    tableName = "recording",
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = ["id"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("patientId", /*unique = true*/)]
)
data class RecordingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val patientId: Long,
    val filename: String,
    val durationMillis: Int,
    val cutStartMillis: Int,
    val cutEndMillis: Int,
    val timestamp: Instant
)

fun RecordingEntity.toDomain(context: Context) = Recording(
    id = id,
    patientId = patientId,
    filename = filename,
    durationMillis = durationMillis,
    cutStart = cutStartMillis,
    cutEnd = cutEndMillis,
    timestamp = timestamp,
    file = File(RecordingUtils.getRecordingsDir(context), filename)
)

fun Recording.toEntity() = RecordingEntity(
    id = id,
    patientId = patientId,
    filename = filename,
    durationMillis = durationMillis,
    cutStartMillis = cutStart,
    cutEndMillis = cutEnd,
    timestamp = timestamp
)
