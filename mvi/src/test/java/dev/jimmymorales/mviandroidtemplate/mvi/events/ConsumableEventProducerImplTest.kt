package dev.jimmymorales.mviandroidtemplate.mvi.events

import app.cash.turbine.test
import dev.jimmymorales.mviandroidtemplate.mvi.UIEvent
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ConsumableEventProducerImplTest {

    @Test
    fun `given event is consumed when second subscriber then event is not emitted`() = runBlocking {
        val consumableEventProducer = ConsumableEventProducerImpl<TestViewEvent>()
        // First subscriber gets event
        consumableEventProducer.events.test {

            val expectedEvent = TestViewEvent("test_data")
            consumableEventProducer.triggerEvent(expectedEvent)

            val receivedEvent = expectItem()
            receivedEvent shouldBe expectedEvent
        }

        // Second subscriber gets no events
        consumableEventProducer.events.test {
            expectNoEvents()
        }
    }
}

private data class TestViewEvent(val data: String) : UIEvent
