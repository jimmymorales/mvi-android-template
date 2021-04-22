package dev.jimmymorales.mviandroidtemplate.mvi.events

import dev.jimmymorales.mviandroidtemplate.mvi.UIEvent
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction to provide view events (UI Side Effects) in a MVI Architecture to a view.
 */
interface ViewEventFlow<EVENT : UIEvent> {
    val events: Flow<EVENT>
}
