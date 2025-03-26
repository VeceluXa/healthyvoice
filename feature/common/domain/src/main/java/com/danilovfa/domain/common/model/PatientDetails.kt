package com.danilovfa.domain.common.model

data class PatientDetails(
    val patient: Patient,
    val analyzes: List<Analysis>
)
