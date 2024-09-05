package com.danilovfa.feature.analyze

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.feature.analyze.AnalyzeComponent.Output

class DefaultAnalyzeComponent(
    private val storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : AnalyzeComponent, ComponentContext by componentContext {
}