package com.danilovfa.core.base.presentation.event

interface EventsDispatcher {
    val events: EventQueue

    fun offerEvent(event: Event) {
        events.offerEvent(event)
    }
}