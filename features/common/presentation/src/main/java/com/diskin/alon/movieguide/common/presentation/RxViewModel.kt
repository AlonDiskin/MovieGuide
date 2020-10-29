package com.diskin.alon.movieguide.common.presentation

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Base class for [ViewModel] implementations that subscribe to Rx observables.
 */
abstract class RxViewModel : ViewModel() {

    protected val disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        // Dispose all rx subscriptions
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}