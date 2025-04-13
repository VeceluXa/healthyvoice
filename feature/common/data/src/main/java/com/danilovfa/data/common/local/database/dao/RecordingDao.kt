package com.danilovfa.data.common.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.danilovfa.data.common.local.database.model.RecordingEntity

@Dao
interface RecordingDao {

    @Insert
    suspend fun addRecording(recording: RecordingEntity): Long

    @Query("SELECT * FROM recording WHERE id = :id LIMIT 1")
    suspend fun getRecording(id: Long): RecordingEntity?

    @Query("DELETE FROM recording WHERE id = :id")
    suspend fun deleteRecording(id: Long)

    @Update
    suspend fun updateRecording(recording: RecordingEntity)
}