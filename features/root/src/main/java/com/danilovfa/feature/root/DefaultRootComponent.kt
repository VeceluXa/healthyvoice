package com.danilovfa.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.data.common.model.AudioData
import com.danilovfa.feature.analyze.AnalyzeComponent
import com.danilovfa.feature.analyze.DefaultAnalyzeComponent
import com.danilovfa.feature.cut.CutComponent
import com.danilovfa.feature.cut.DefaultCutComponent
import com.danilovfa.feature.record.DefaultRecordComponent
import com.danilovfa.feature.record.RecordComponent
import com.danilovfa.feature.root.RootComponent.Child
import kotlinx.serialization.Serializable

class DefaultRootComponent(
    private val storeFactory: StoreFactory,
    private val componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    private val stack = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Record,
        handleBackButton = false,
        childFactory = ::child
    )

    override val childStack = stack

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

    @Serializable
    sealed class Config {

        @Serializable
        data object Record : Config()

        @Serializable
        data class Cut(val audioData: AudioData) : Config()

        @Serializable
        data class Analyze(val audioData: AudioData) : Config()
    }
}