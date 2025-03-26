package com.danilovfa.presentation.patient.detail

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.presentation.patient.detail.PatientDetailComponent.Output

internal class DefaultPatientDetailComponent(
    private val patientId: Long,
    storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : PatientDetailComponent {
}