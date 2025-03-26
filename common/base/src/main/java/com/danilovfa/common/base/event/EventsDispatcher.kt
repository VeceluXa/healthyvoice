package com.danilovfa.common.base.event

import com.danilovfa.common.base.event.Event
import com.danilovfa.common.base.event.EventDelegate

interface EventsDispatcher {
    val events: EventDelegate

    fun offerEvent(event: Event) {
        events.offerEvent(event)
    }
}