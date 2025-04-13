package com.danilovfa.presentation.record.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.presentation.record.cut.CutComponent
import com.danilovfa.presentation.record.cut.DefaultCutComponent
import com.danilovfa.presentation.record.main.DefaultRecordComponent
import com.danilovfa.presentation.record.main.RecordComponent
import com.danilovfa.presentation.record.root.RecordRootComponent.Child
import com.danilovfa.presentation.record.root.RecordRootComponent.Output
import kotlinx.serialization.Serializable

class DefaultRecordRootComponent(
    private val patientId: Long,
    private val storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : RecordRootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialStack = { listOf(Config.Record) },
        childFactory = ::child
    )

    private fun child(config: Config, componentContext: ComponentContext): Child = when (config) {
        is Config.Cut -> Child.Cut(
            DefaultCutComponent(
                recordingId = config.recordingId,
                storeFactory = storeFactory,
                componentContext = componentContext,
                output = ::onCutOutput
            )
        )

        Config.Record -> Child.Record(
            DefaultRecordComponent(
                patientId = patientId,
                storeFactory = storeFactory,
                componentContext = componentContext,
                output = ::onRecordOutput
            )
        )
    }

    private fun onRecordOutput(output: RecordComponent.Output) = when (output) {
        is RecordComponent.Output.NavigateCut -> navigation.pushNew(Config.Cut(recordingId = output.recordingId))
        RecordComponent.Output.NavigateBack -> output(Output.NavigateBack)
    }

    private fun onCutOutput(output: CutComponent.Output) = when (output) {
        is CutComponent.Output.Analyze -> output(Output.NavigateAnalysis(output.recordingId))
        CutComponent.Output.NavigateBack -> navigation.pop()
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object Record : Config()

        @Serializable
        data class Cut(val recordingId: Long) : Config()
    }
}