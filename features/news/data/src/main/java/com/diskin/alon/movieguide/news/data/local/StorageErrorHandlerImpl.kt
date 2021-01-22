package com.diskin.alon.movieguide.news.data.local

import com.diskin.alon.movieguide.common.appservices.AppError
import javax.inject.Inject

class StorageErrorHandlerImpl @Inject constructor() : StorageErrorHandler {

    override fun handle(throwable: Throwable): AppError {
        return AppError("Unexpected db error",false)
    }
}