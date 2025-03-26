package com.danilovfa.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.domain.record.repository.model.AudioData
import com.danilovfa.presentation.cut.CutComponent
import com.danilovfa.presentation.cut.DefaultCutComponent
import com.danilovfa.presentation.record.DefaultRecordComponent
import com.danilovfa.presentation.record.RecordComponent
import com.danilovfa.presentation.analysis.AnalyzeComponent
import com.danilovfa.presentation.analysis.DefaultAnalyzeComponent
import com.danilovfa.presentation.patient.root.DefaultRootPatientComponent
import com.danilovfa.presentation.patient.root.RootPatientComponent
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
        Config.Record -> Child.Record(
            DefaultRecordComponent(
                storeFactory = storeFactory,
                componentContext = componentContext,
                output = ::onRecordOutput
            )
        )

        is Config.Cut -> Child.Cut(
            DefaultCutComponent(
                audioData = config.audioData,
                storeFactory = storeFactory,
                componentContext = componentContext,
                output = ::onCutOutput
            )
        )

        is Config.Analyze -> Child.Analyze(
            DefaultAnalyzeComponent(
                audioData = config.audioData,
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
    }

    private fun onRecordOutput(output: RecordComponent.Output) = when (output) {
        is RecordComponent.Output.Analyze -> navigation.pushNew(Config.Cut(output.audioData))
    }

    private fun onCutOutput(output: CutComponent.Output) = when (output) {
        is CutComponent.Output.Analyze -> navigation.pushNew(Config.Analyze(output.data))
        CutComponent.Output.NavigateBack -> navigation.pop()
    }

    private fun onAnalyzeOutput(output: AnalyzeComponent.Output) = when (output) {
        AnalyzeComponent.Output.NavigateBack -> navigation.pop()
    }

    private fun onPatientOutput(output: RootPatientComponent.Output) = when (output) {
        is RootPatientComponent.Output.NavigateAnalysis -> TODO()
        is RootPatientComponent.Output.NavigateRecord -> navigation.pushNew(Config.Record)
    }

    @Serializable
    sealed class Config {

        @Serializable
        data object Record : Config()

        @Serializable
        data class Cut(val audioData: AudioData) : Config()

        @Serializable
        data class Analyze(val audioData: AudioData) : Config()

        @Serializable
        data object Patient : Config()
    }
}