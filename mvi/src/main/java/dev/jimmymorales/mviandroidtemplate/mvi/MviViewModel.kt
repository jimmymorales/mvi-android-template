package dev.jimmymorales.mviandroidtemplate.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
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

    private val internalEvents = MutableSharedFlow<ConsumableEvent<EVENT>>(
        extraBufferCapacity = FLOW_BUFFER_CAPACITY
    )
    val events: Flow<EVENT> = internalEvents.mapNotNull { event -> event.getContentIfNotHandled() }

    private val internalState = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = internalState.asStateFlow()

    private val viewIntents = MutableSharedFlow<INTENT>(extraBufferCapacity = FLOW_BUFFER_CAPACITY)

    private val reduceActions = MutableSharedFlow<ACTION>(
        extraBufferCapacity = FLOW_BUFFER_CAPACITY
    )

    init {
        viewIntents
            .onEach { intent ->
                Timber.v("Processing intent = $intent")
                handleIntent(intent)
            }
            .launchIn(viewModelScope)

        reduceActions
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
        viewIntents.emit(intent)
    }

    protected suspend fun onAction(action: ACTION) {
        reduceActions.emit(action)
    }

    protected suspend fun triggerEvent(event: EVENT) {
        internalEvents.emit(ConsumableEvent(event))
    }

    protected abstract suspend fun handleIntent(intent: INTENT)

    protected abstract suspend fun reduce(state: STATE, action: ACTION): STATE
}

private const val FLOW_BUFFER_CAPACITY = 64

