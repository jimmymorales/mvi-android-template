package dev.jimmymorales.mviandroidtemplate.mvi.events

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ConsumableEventTest {

    @Test
    fun `given event is not consumed then returns event`(): Unit = runBlocking {
        checkAll<Boolean> { content ->
            val event = ConsumableEvent(content)

            event.getContentIfNotHandled() shouldBe content
        }
    }

    @Test
    fun `given event is consumed then returns null`(): Unit = runBlocking {
        checkAll<Boolean> { content ->
            val event = ConsumableEvent(content)

            // Consume event
            event.getContentIfNotHandled()

            event.getContentIfNotHandled().shouldBeNull()
        }
    }
}
