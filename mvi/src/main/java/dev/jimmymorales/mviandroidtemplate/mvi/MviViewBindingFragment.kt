package dev.jimmymorales.mviandroidtemplate.mvi


import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@ExperimentalCoroutinesApi
abstract class MviViewBindingFragment<
        VB : ViewBinding,
        STATE : ViewState,
        INTENT : ViewIntent,
        ACTION : VMAction,
        EVENT : Event,
        VM : MviViewModel<STATE, INTENT, ACTION, EVENT>>(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

    protected abstract val viewModel: VM

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = bindView(view)
        initUI(binding)

        viewModel.state
            .flowWithLifecycle(fragment = this, Lifecycle.State.STARTED)
            .onEach { state -> render(binding, state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.events
            .flowWithLifecycle(fragment = this, Lifecycle.State.STARTED)
            .onEach { event -> handleEvent(event) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    @CallSuper
    protected open fun handleEvent(event: EVENT) {
        Timber.v("Handling event from UI -> $event")
    }

    protected abstract fun bindView(view: View): VB
    protected abstract fun render(binding: VB, viewState: STATE)
    protected abstract fun initUI(binding: VB)
    protected fun dispatchViewIntentWhenResumed(viewIntent: INTENT) {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.onIntent(viewIntent)
        }
    }
}
