package com.diskin.alon.movieguide.reviews.data.local

import com.diskin.alon.movieguide.common.appservices.AppError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageErrorHandlerImpl @Inject constructor() : StorageErrorHandler {

    override fun handle(throwable: Throwable): AppError {
        return AppError("Unexpected db error",false)
    }
}