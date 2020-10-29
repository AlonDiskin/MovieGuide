package com.diskin.alon.movieguide.common.presentation

sealed class LoadableState<T>(val state: T?) {

    data class Success<T>(val data: T) : LoadableState<T>(data)

    data class Error<T>(val error: Throwable) : LoadableState<T>(null)

    class Loading<T>(val data: T?): LoadableState<T>(data)
}