package com.danilovfa.data.common.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.domain.common.model.Sex
import kotlinx.datetime.LocalDate

@Entity("patient")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val birthDate: LocalDate,
    val address: String,
    val sex: Sex,
    val note: String
)

fun PatientEntity.toDomain() = Patient(
    id = id,
    name = name,
    birthDate = birthDate,
    address = address,
    sex = sex,
    note = note
)

fun Patient.toEntity() = PatientEntity(
    id = id,
    name = name,
    birthDate = birthDate,
    address = address,
    sex = sex,
    note = note
)