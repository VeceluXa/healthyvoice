package com.danilovfa.healthyvoice.di

import com.danilovfa.data.analysis.di.dataAnalysisModule
import com.danilovfa.data.common.di.dataCommonModule
import com.danilovfa.data.export.di.dataExportModule
import com.danilovfa.data.patient.di.dataPatientModule
import com.danilovfa.data.record.di.dataRecordModule
import com.danilovfa.export.presentation.di.presentationExportModule

internal val modules = listOf(
    dataPatientModule,
    dataCommonModule,
    dataRecordModule,
    dataAnalysisModule,
    dataExportModule,
    presentationExportModule
)