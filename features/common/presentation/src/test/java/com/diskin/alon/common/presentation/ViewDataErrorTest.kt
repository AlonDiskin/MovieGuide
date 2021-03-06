package com.diskin.alon.common.presentation

import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/**
 * [ErrorViewData] unit test class.
 */
class ViewDataErrorTest {

    @Test
    fun invokeRetryPropertyWhenRetriableErrorRetried() {
        // Test case fixture
        val retryFunc = mockk<() -> (Unit)>()

        every { retryFunc.invoke() } returns Unit

        // Given an initialized retriable view data error
        val error = ErrorViewData.Retriable("message",retryFunc)

        // When error is retried by client
        error.retry()

        // Then error should invoke private retry function property
        verify { retryFunc.invoke() }
    }
}