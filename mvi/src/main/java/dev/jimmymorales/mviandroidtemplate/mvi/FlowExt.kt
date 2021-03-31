package dev.jimmymorales.mviandroidtemplate.mvi

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * Flow operator that emits values from `this` upstream Flow when the [fragment]'s view lifecycle is
 * at least at [minActiveState] state. The emissions will be stopped when the lifecycle state
 * falls below [minActiveState] state.
 *
 * The flow will automatically start and cancel collecting from `this` upstream flow as the
 * [fragment]'s view lifecycle moves in and out of the target state.
 *
 * If [this] upstream Flow completes emitting items, `flowWithLifecycle` will trigger the flow
 * collection again when the [minActiveState] state is reached.
 *
 * This is NOT a terminal operator. This operator is usually followed by [collect], or
 * [onEach] and launchIn to process the emitted values.
 *
 * Note: this operator creates a hot flow that only closes when the [fragment]'s view lifecycle is
 * destroyed or the coroutine that collects from the flow is cancelled.
 *
 * ```
 * class MyFragment : Fragment(R.layout.fragment_main) {
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         /* ... */
 *         // Launches a coroutine that collects items from a flow when the Fragment's view
 *         // is at least started. It will automatically cancel when the fragment's view is stopped
 *         // and start collecting again whenever it's started again.
 *         flow
 *             .flowWithLifecycle(fragment = this, Lifecycle.State.STARTED)
 *             .onEach {
 *                 // Consume flow emissions
 *             }
 *             .launchIn(viewLifecycleOwner.lifecycleScope)
 *     }
 * }
 * ```
 *
 * Warning: [Lifecycle.State.INITIALIZED] is not allowed in this API. Passing it as a
 * parameter will throw an [IllegalArgumentException].
 *
 * @param fragment The [Lifecycle] where the restarting collecting from `this` flow work will be
 * kept alive.
 * @param minActiveState [Lifecycle.State] in which the upstream flow gets collected. The
 * collection will stop if the lifecycle falls below that state, and will restart if it's in that
 * state again.
 * @return [Flow] that only emits items from `this` upstream flow when the [fragment]'s view
 * lifecycle is at least in the [minActiveState].
 */
@ExperimentalCoroutinesApi
internal fun <T> Flow<T>.flowWithLifecycle(
    fragment: Fragment,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): Flow<T> = callbackFlow {
    fragment.viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
        this@flowWithLifecycle.collect {
            send(it)
        }
    }
    close()
}

/**
 * Runs the given [block] in a new coroutine when `this` [Lifecycle] is at least at [state] and
 * suspends the execution until `this` [Lifecycle] is [Lifecycle.State.DESTROYED].
 *
 * The [block] will cancel and re-launch as the lifecycle moves in and out of the target state.
 *
 * Warning: [Lifecycle.State.INITIALIZED] is not allowed in this API. Passing it as a
 * parameter will throw an [IllegalArgumentException].
 *
 * @param state [Lifecycle.State] in which `block` runs in a new coroutine. That coroutine
 * will cancel if the lifecycle falls below that state, and will restart if it's in that state
 * again.
 * @param block The block to run when the lifecycle is at least in [state] state.
 */
private suspend fun Lifecycle.repeatOnLifecycle(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
) {
    require(state !== Lifecycle.State.INITIALIZED) {
        "repeatOnLifecycle cannot start work with the INITIALIZED lifecycle state."
    }

    if (currentState === Lifecycle.State.DESTROYED) {
        return
    }

    coroutineScope {
        withContext(Dispatchers.Main.immediate) {
            // Check the current state of the lifecycle as the previous check is not guaranteed
            // to be done on the main thread.
            if (currentState === Lifecycle.State.DESTROYED) return@withContext

            // Instance of the running repeating coroutine
            var launchedJob: Job? = null

            // Registered observer
            var observer: LifecycleEventObserver? = null

            try {
                // Suspend the coroutine until the lifecycle is destroyed or
                // the coroutine is cancelled
                suspendCancellableCoroutine<Unit> { cont ->
                    // Lifecycle observers that executes `block` when the lifecycle reaches certain state, and
                    // cancels when it moves falls below that state.
                    val startWorkEvent = Lifecycle.Event.upTo(state)
                    val cancelWorkEvent = Lifecycle.Event.downFrom(state)
                    observer = LifecycleEventObserver { _, event ->
                        if (event == startWorkEvent) {
                            // Launch the repeating work preserving the calling context
                            launchedJob = this@coroutineScope.launch(block = block)
                            return@LifecycleEventObserver
                        }
                        if (event == cancelWorkEvent) {
                            launchedJob?.cancel()
                            launchedJob = null
                        }
                        if (event == Lifecycle.Event.ON_DESTROY) {
                            cont.resume(Unit)
                        }
                    }
                    this@repeatOnLifecycle.addObserver(observer as LifecycleEventObserver)
                }
            } finally {
                launchedJob?.cancel()
                observer?.let {
                    this@repeatOnLifecycle.removeObserver(it)
                }
            }
        }
    }
}
