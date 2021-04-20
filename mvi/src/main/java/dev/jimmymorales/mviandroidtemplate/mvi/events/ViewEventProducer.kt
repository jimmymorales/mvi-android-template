package dev.jimmymorales.mviandroidtemplate.mvi.events

import dev.jimmymorales.mviandroidtemplate.mvi.UIEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.mapNotNull

interface ViewEventProducer<EVENT : UIEvent> : ViewEventFlow<EVENT> {
    suspend fun triggerEvent(event: EVENT)
}

/**
 * Default implementation of a [ViewEventProducer]. It will make sure the events propagated only
 * once to the subscribers.
 */
internal class ConsumableEventProducerImpl<EVENT : UIEvent> : ViewEventProducer<EVENT> {

    private val internalEvents = MutableSharedFlow<ConsumableEvent<EVENT>>(extraBufferCapacity = 64)

    override val events: Flow<EVENT> = internalEvents
            .mapNotNull { event -> event.getContentIfNotHandled() }

    override suspend fun triggerEvent(event: EVENT) {
        internalEvents.emit(ConsumableEvent(event))
    }
}
