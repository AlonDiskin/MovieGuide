package com.diskin.alon.common.presentation

import androidx.lifecycle.ViewModel
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.google.common.truth.Truth.assertThat
import io.reactivex.disposables.Disposable
import org.junit.Test

class RxViewModelTest {

    private val viewModel = object : RxViewModel() {}

    @Test
    fun clearDisposableWhenViewModelCleared() {
        // Given an initialized view model implementation

        // When view model is cleared
        val method = ViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)

        // Then all rx subscriptions should be disposed by view model
        val field = RxViewModel::class.java.getDeclaredField("disposable")
        field.isAccessible = true
        val disposable = field.get(viewModel) as Disposable

        assertThat(disposable.isDisposed).isTrue()
    }
}