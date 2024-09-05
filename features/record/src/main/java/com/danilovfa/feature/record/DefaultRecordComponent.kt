package com.danilovfa.feature.record

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.danilovfa.feature.record.RecordComponent.Output

class DefaultRecordComponent(
    private val storeFactory: StoreFactory,
    componentContext: ComponentContext,
    private val output: (Output) -> Unit
) : RecordComponent, ComponentContext by componentContext {
}