package dev.jimmymorales.mviandroidtemplate.mvi

import app.cash.turbine.test
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class MviViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `check initial state`() {
        val vm = TestMviViewModel()

        vm.state.value shouldBe TestViewState.Initial
    }

    @Test
    fun `given reduce intent then state should be final`() = mainCoroutineRule.runBlockingTest {
        val vm = TestMviViewModel()

        vm.onIntent(TestViewIntent.ReduceState)

        vm.state.value shouldBe TestViewState.Final
        vm.lastReduceAction shouldBe TestReduceAction.Reduce
    }

    @Test
    fun `given event intent then state should be initial and event should be triggered`() =
        mainCoroutineRule.runBlockingTest {
            val vm = TestMviViewModel()
            vm.events.test {
                vm.onIntent(TestViewIntent.TriggerEvent)

                vm.state.value shouldBe TestViewState.Initial
                vm.lastReduceAction.shouldBeNull()

                expectItem() shouldBe TestViewEvent.Test
            }
        }
}

sealed class TestViewState : UIState {
    object Initial : TestViewState()
    object Final : TestViewState()
}

sealed class TestViewIntent : ViewIntent {
    object ReduceState : TestViewIntent()
    object TriggerEvent : TestViewIntent()
}

sealed class TestReduceAction : ReduceAction {
    object Reduce : TestReduceAction()
}

sealed class TestViewEvent : UIEvent {
    object Test : TestViewEvent()
}

@ExperimentalCoroutinesApi
private class TestMviViewModel :
    MviViewModel<TestViewState, TestViewIntent, TestReduceAction, TestViewEvent>(
        TestViewState.Initial
    ) {

    var lastReduceAction: TestReduceAction? = null
        private set

    override suspend fun handleIntent(intent: TestViewIntent) {
        when (intent) {
            TestViewIntent.ReduceState -> onAction(TestReduceAction.Reduce)
            TestViewIntent.TriggerEvent -> triggerEvent(TestViewEvent.Test)
        }
    }

    override suspend fun reduce(state: TestViewState, action: TestReduceAction): TestViewState =
        when (action) {
            TestReduceAction.Reduce ->
                if (state == TestViewState.Initial) TestViewState.Final else state
        }.also { lastReduceAction = action }
}
