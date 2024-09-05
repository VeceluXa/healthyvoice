package com.danilovfa.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.feature.analyze.AnalyzeComponent
import com.danilovfa.feature.analyze.DefaultAnalyzeComponent
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
        is Config.Analyze -> Child.Analyze(
            DefaultAnalyzeComponent(
                storeFactory = storeFactory,
                componentContext = componentContext,
                output = ::onAnalyzeOutput
            )
        )

        Config.Record -> Child.Record(
            DefaultRecordComponent(
                storeFactory = storeFactory,
                componentContext = componentContext,
                output = ::onRecordOutput
            )
        )
    }

    private fun onRecordOutput(output: RecordComponent.Output) = when (output) {
        is RecordComponent.Output.Analyze -> navigation.pushNew(Config.Analyze(output.filename))
    }

    private fun onAnalyzeOutput(output: AnalyzeComponent.Output) = when (output) {
        AnalyzeComponent.Output.NavigateBack -> navigation.pop()
    }

    @Serializable
    sealed class Config {

        @Serializable
        data object Record : Config()

        @Serializable
        data class Analyze(val filename: String) : Config()
    }
}