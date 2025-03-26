package com.danilovfa.healthyvoice.di

import com.danilovfa.data.common.di.dataCommonModule
import com.danilovfa.data.patient.di.dataPatientModule
import com.danilovfa.data.record.di.dataRecordModule

internal val modules = listOf(
    dataPatientModule,
    dataCommonModule,
    dataRecordModule
)