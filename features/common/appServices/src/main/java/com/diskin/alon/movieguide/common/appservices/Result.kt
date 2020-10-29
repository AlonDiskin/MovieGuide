package com.diskin.alon.movieguide.common.appservices

sealed class Result<T : Any> {

    data class Success<T : Any>(val data: T) : Result<T>()

    data class Error<T : Any>(val error: AppError) : Result<T>()
}