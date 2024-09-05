package com.danilovfa.core.base.presentation.event

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update

class EventQueue {

    private val eventsFlow = MutableStateFlow<List<Event>>(emptyList())

    /** Returns flow of events. */
    @OptIn(ExperimentalCoroutinesApi::class)
    val flow: Flow<Event>
        get() = eventsFlow.flatMapConcat { consumeAll() }

    /** Adds given [event] to the queue. */
    fun offerEvent(event: Event) {
        eventsFlow.update { it + event }
    }

    private fun consumeAll(): Flow<Event> = eventsFlow.getAndUpdate { emptyList() }.asFlow()
}