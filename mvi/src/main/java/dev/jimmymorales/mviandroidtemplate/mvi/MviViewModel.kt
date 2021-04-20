package dev.jimmymorales.mviandroidtemplate.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import timber.log.Timber

interface UIState

interface ViewIntent

interface ReduceAction

interface UIEvent

@ExperimentalCoroutinesApi
abstract class MviViewModel<
    STATE : UIState,
    INTENT : ViewIntent,
    ACTION : ReduceAction,
    EVENT : UIEvent
    >(
    initialState: STATE,
) : ViewModel() {

    private val internalEvents = Channel<ConsumableEvent<EVENT>>(Channel.BUFFERED)
    val events: Flow<EVENT> = internalEvents.receiveAsFlow()
        .mapNotNull { event -> event.getContentIfNotHandled() }

    private val internalState = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = internalState.asStateFlow()

    private val viewIntents = Channel<INTENT>(capacity = Channel.UNLIMITED)

    private val reduceActions = Channel<ACTION>(capacity = Channel.UNLIMITED)

    init {
        viewIntents.consumeAsFlow()
            .onEach { intent ->
                Timber.v("Processing intent = $intent")
                handleIntent(intent)
            }
            .launchIn(viewModelScope)

        reduceActions.consumeAsFlow()
            .scan(initialState) { state, action ->
                Timber.v("Reducing action = $action")
                Timber.v("Old state = $state")
                reduce(state, action)
            }
            .onEach { newState ->
                internalState.value = newState
                Timber.v("New state = $newState")
            }
            .launchIn(viewModelScope)
    }

    suspend fun onIntent(intent: INTENT) {
        viewIntents.send(intent)
    }

    protected suspend fun onAction(action: ACTION) {
        reduceActions.send(action)
    }

    protected suspend fun triggerEvent(event: EVENT) {
        internalEvents.send(ConsumableEvent(event))
    }

    protected abstract suspend fun handleIntent(intent: INTENT)

    protected abstract suspend fun reduce(state: STATE, action: ACTION): STATE
}
