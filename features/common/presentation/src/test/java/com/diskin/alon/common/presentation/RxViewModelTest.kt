package com.diskin.alon.common.presentation

import androidx.lifecycle.ViewModel
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.junit.Test

/**
 * [RxViewModel] unit test class.
 */
class RxViewModelTest {

    // Test subject
    private val viewModel = object : RxViewModel() {}

    @Test
    fun clearDisposableWhenViewModelCleared() {
        // Given an initialized view model implementation

        // When view model is cleared
        val method = ViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)

        // Then all rx subscriptions should be disposed by view model
        val field = RxViewModel::class.java.getDeclaredField("container")
        field.isAccessible = true
        val container = field.get(viewModel) as Disposable

        assertThat(container.isDisposed).isTrue()
    }

    @Test
    fun addSubscriptionToDisposables() {
        // Given an initialized view model

        // When disposable subscription is added to view model
        val method = RxViewModel::class.java.getDeclaredMethod("addSubscription",Disposable::class.java)
        method.isAccessible = true
        method.invoke(viewModel, mockk<Disposable>())

        // Then view model should add subscription to container
        val field = RxViewModel::class.java.getDeclaredField("container")
        field.isAccessible = true
        val container = field.get(viewModel) as CompositeDisposable

        assertThat(container.size()).isEqualTo(1)
    }
}