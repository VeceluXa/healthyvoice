package com.danilovfa.domain.common.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Patient(
    val id: Long = 0L,
    val name: String,
    val birthDate: LocalDate,
    val address: String,
    val sex: Sex,
    val note: String = ""
)
