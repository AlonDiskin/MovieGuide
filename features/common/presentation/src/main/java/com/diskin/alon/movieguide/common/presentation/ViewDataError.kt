package com.diskin.alon.movieguide.common.presentation

sealed class ViewDataError(val reason: String) : Throwable() {

    class Retriable(reason: String,private val retry: () -> (Unit)): ViewDataError(reason) {

        fun retry() {
            retry.invoke()
        }
    }

    class NoTRetriable(reason: String): ViewDataError(reason)
}