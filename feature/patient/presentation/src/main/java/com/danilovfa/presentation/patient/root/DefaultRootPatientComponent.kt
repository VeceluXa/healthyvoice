package com.danilovfa.presentation.patient.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.presentation.patient.create.DefaultPatientCreateComponent
import com.danilovfa.presentation.patient.create.PatientCreateComponent
import com.danilovfa.presentation.patient.detail.DefaultPatientDetailComponent
import com.danilovfa.presentation.patient.detail.PatientDetailComponent
import com.danilovfa.presentation.patient.list.DefaultPatientListComponent
import com.danilovfa.presentation.patient.list.PatientListComponent
import com.danilovfa.presentation.patient.root.RootPatientComponent.Child
import com.danilovfa.presentation.patient.root.RootPatientComponent.Output
import kotlinx.serialization.Serializable

class DefaultRootPatientComponent(
    private val storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : RootPatientComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    override val childStack = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialStack = { listOf(Config.List) },
        handleBackButton = false,
        childFactory = ::child
    )

    private fun child(config: Config, componentContext: ComponentContext): Child = when (config) {
        is Config.Create -> Child.Create(
            DefaultPatientCreateComponent(
                patient = config.patient,
                storeFactory = storeFactory,
                componentContext = componentContext,
                output = ::onCreateOutput
            )
        )
        is Config.Detail -> Child.Detail(
            DefaultPatientDetailComponent(
                patientId = config.patientId,
                storeFactory = storeFactory,
                componentContext = componentContext,
                output = ::onDetailOutput
            )
        )
        Config.List -> Child.List(
            DefaultPatientListComponent(
                storeFactory = storeFactory,
                componentContext = componentContext,
                output = ::onListOutput
            )
        )
    }

    private fun onListOutput(output: PatientListComponent.Output) = when (output) {
        PatientListComponent.Output.NavigatePatientCreate -> navigation.pushNew(Config.Create(null))
        is PatientListComponent.Output.NavigatePatientDetails -> navigation.pushNew(Config.Detail(output.patientId))
    }

    private fun onCreateOutput(output: PatientCreateComponent.Output) = when (output) {
        PatientCreateComponent.Output.NavigateBack -> navigation.pop()
        is PatientCreateComponent.Output.NavigatePatient -> navigation.replaceCurrent(Config.Detail(output.patientId))
    }

    private fun onDetailOutput(output: PatientDetailComponent.Output) = when (output) {
        is PatientDetailComponent.Output.NavigateAnalysis -> output(Output.NavigateAnalysis(output.analysisId))
        is PatientDetailComponent.Output.NavigateRecord -> output(Output.NavigateRecord(output.patientId))
        PatientDetailComponent.Output.NavigateBack -> navigation.pop()
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object List : Config()

        @Serializable
        data class Create(val patient: Patient?) : Config()

        @Serializable
        data class Detail(val patientId: Long) : Config()
    }
}