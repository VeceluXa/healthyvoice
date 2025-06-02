package com.danilovfa.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.presentation.analysis.AnalyzeComponent
import com.danilovfa.presentation.analysis.DefaultAnalyzeComponent
import com.danilovfa.presentation.patient.root.DefaultRootPatientComponent
import com.danilovfa.presentation.patient.root.RootPatientComponent
import com.danilovfa.presentation.record.root.DefaultRecordRootComponent
import com.danilovfa.presentation.record.root.RecordRootComponent
import com.danilovfa.presentation.root.RootComponent.Child
import kotlinx.serialization.Serializable

class DefaultRootComponent(
    private val storeFactory: StoreFactory,
    private val componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    private val stack = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialStack = { listOf(Config.Patient) },
        handleBackButton = false,
        childFactory = ::child
    )

    override val childStack: Value<ChildStack<*, Child>> = stack

    private fun child(config: Config, componentContext: ComponentContext): Child = when (config) {

        is Config.Analysis -> Child.Analyze(
            DefaultAnalyzeComponent(
                recordingId = config.recordingId,
                storeFactory = storeFactory,
                componentContext = componentContext,
                output = ::onAnalyzeOutput
            )
        )

        Config.Patient -> Child.Patient(
            DefaultRootPatientComponent(
                storeFactory = storeFactory,
                componentContext = componentContext,
                output = ::onPatientOutput
            )
        )

        is Config.Record -> Child.Recording(
            DefaultRecordRootComponent(
                patientId = config.patientId,
                storeFactory = storeFactory,
                componentContext = componentContext,
                output = ::onRecordOutput
            )
        )
    }


    private fun onAnalyzeOutput(output: AnalyzeComponent.Output) = when (output) {
        AnalyzeComponent.Output.NavigateBack -> navigation.pop()
    }

    private fun onPatientOutput(output: RootPatientComponent.Output) = when (output) {
        is RootPatientComponent.Output.NavigateAnalysis -> navigation.pushNew(Config.Analysis(recordingId = output.recordingId))
        is RootPatientComponent.Output.NavigateRecord -> navigation.pushNew(Config.Record(output.patientId))
    }

    private fun onRecordOutput(output: RecordRootComponent.Output) = when (output) {
        is RecordRootComponent.Output.NavigateAnalysis -> {
            navigation.replaceCurrent(Config.Analysis(recordingId = output.recordingId))
        }

        RecordRootComponent.Output.NavigateBack -> navigation.pop()
    }

    @Serializable
    sealed class Config {

        @Serializable
        data class Record(val patientId: Long) : Config()

        @Serializable
        data class Analysis(val recordingId: Long) : Config()

        @Serializable
        data object Patient : Config()
    }
}