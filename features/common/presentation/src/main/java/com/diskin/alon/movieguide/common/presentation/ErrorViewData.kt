package com.diskin.alon.movieguide.common.presentation

sealed class ErrorViewData {

    object NoError : ErrorViewData()

    data class Retriable(val reason: String,private val retry: () -> (Unit)): ErrorViewData() {
        fun retry() {
            retry.invoke()
        }
    }

    data class NotRetriable(val reason: String): ErrorViewData()
}