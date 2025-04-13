package com.danilovfa.data.common.local.database.model

import android.content.Context
import androidx.room.Embedded
import androidx.room.Relation
import com.danilovfa.domain.common.model.RecordingAnalysis

data class RecordingAnalysisEntity(
    @Embedded val recording: RecordingEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recordingId"
    )
    val analysis: AnalysisEntity?
)

fun RecordingAnalysisEntity.toDomain(context: Context) = RecordingAnalysis(
    recording = recording.toDomain(context),
    analysis = analysis?.toDomain()
)