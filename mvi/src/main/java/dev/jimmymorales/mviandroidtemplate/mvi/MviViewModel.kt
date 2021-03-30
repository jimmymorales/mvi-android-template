package dev.jimmymorales.mviandroidtemplate.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

abstract class MviViewModel<ViewState, ViewIntent, VMAction, VMEvent>(
    initialState: ViewState,
) : ViewModel() {

    private val internalEvents = Channel<Event<VMEvent>>(Channel.BUFFERED)
    val events: Flow<VMEvent> = internalEvents.receiveAsFlow()
        .mapNotNull { event -> event.getContentIfNotHandled() }

    private val internalState = MutableStateFlow(initialState)
    val state: StateFlow<ViewState> = internalState.asStateFlow()

    private val viewIntents = Channel<ViewIntent>(capacity = Channel.UNLIMITED)

    private val viewModelActions = Channel<VMAction>(capacity = Channel.UNLIMITED)

    init {
        viewIntents.consumeAsFlow()
            .onEach { intent ->
                Timber.v("Processing intent = $intent")
                handleIntent(intent)
            }
            .launchIn(viewModelScope)

        viewModelActions.consumeAsFlow()
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

    suspend fun onIntent(intent: ViewIntent) {
        viewIntents.send(intent)
    }

    protected suspend fun triggerEvent(event: VMEvent) {
        internalEvents.send(Event(event))
    }

    protected suspend fun onAction(action: VMAction) {
        viewModelActions.send(action)
    }

    protected abstract suspend fun reduce(state: ViewState, action: VMAction): ViewState

    protected abstract suspend fun handleIntent(intent: ViewIntent)
}
