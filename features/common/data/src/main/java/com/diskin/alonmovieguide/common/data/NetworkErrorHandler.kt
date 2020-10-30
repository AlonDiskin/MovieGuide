package com.diskin.alonmovieguide.common.data

import com.diskin.alon.movieguide.common.appservices.AppError
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkErrorHandler @Inject constructor() {

    companion object {
        // Network error messages
        const val ERR_DEVICE_NETWORK = "Network error,check device connectivity"
        const val ERR_API_SERVER = "Server currently unavailable"
        const val ERR_UNKNOWN_NETWORK = "Unknown network error"
    }

    fun handle(throwable: Throwable): AppError {
        return when (throwable) {
            // Retrofit calls that return the body type throw either IOException for
            // network failures, or HttpException for any non-2xx HTTP status codes.
            // This code reports all errors to the UI
            is IOException -> AppError(ERR_DEVICE_NETWORK,true)
            is HttpException -> AppError(ERR_API_SERVER,true)
            else -> AppError(ERR_UNKNOWN_NETWORK,false)
        }
    }
}