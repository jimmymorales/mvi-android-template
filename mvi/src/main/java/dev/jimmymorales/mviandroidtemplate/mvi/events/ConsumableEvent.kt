package dev.jimmymorales.mviandroidtemplate.mvi.events

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Used as a wrapper for data that is exposed via a Flow that represents an event.
 */
class ConsumableEvent<out T>(private val content: T) {
    private val hasBeenHandled = AtomicBoolean(false)

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled.compareAndSet(false, true)) {
            content
        } else {
            null
        }
    }
}
