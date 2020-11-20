package com.diskin.alonmovieguide.common.data

import com.diskin.alon.movieguide.common.appservices.AppError
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import java.io.IOException

/**
 * [NetworkErrorHandler] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class NetworkErrorHandlerTest {

    // Test subject
    private lateinit var handler: NetworkErrorHandler

    @Before
    fun setUp() {
        handler = NetworkErrorHandler()
    }

    @Test
    @Parameters(method = "errorParams")
    fun createAppErrorWhenAskedToHandleError(error: Throwable, appError: AppError) {
        // Given an initialized han

        // When handler is asked to  handle an error
        val actual = handler.handle(error)

        // Then handler should handle error and create the correct app error instance for it
        assertThat(actual).isEqualTo(appError)
    }

    private fun errorParams() =
        arrayOf(
            arrayOf(IOException(),AppError(NetworkErrorHandler.ERR_DEVICE_NETWORK,true)),
            arrayOf(mockk<HttpException>(),AppError(NetworkErrorHandler.ERR_API_SERVER,true)),
            arrayOf(Throwable(),AppError(NetworkErrorHandler.ERR_UNKNOWN_NETWORK,false))
        )
}