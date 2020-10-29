package com.diskin.alon.movieguide.common.presentation

import com.diskin.alon.movieguide.common.appservices.AppError

sealed class LoadState {

    object Loading : LoadState()

    object Success : LoadState()

    data class Error(val error: AppError) : LoadState()
}