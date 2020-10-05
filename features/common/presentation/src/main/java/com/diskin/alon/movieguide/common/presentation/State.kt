package com.diskin.alon.movieguide.common.presentation

sealed class State<T> {

    data class Success<T>(val data: T) : State<T>()

    data class Error<T>(val error: Throwable) : State<T>()

    class Loading<T>(): State<T>()
}