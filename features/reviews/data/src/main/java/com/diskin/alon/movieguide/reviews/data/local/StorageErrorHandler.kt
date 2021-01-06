package com.diskin.alon.movieguide.reviews.data.local

import com.diskin.alon.movieguide.common.appservices.AppError

interface StorageErrorHandler {

    fun handle(throwable: Throwable): AppError
}
