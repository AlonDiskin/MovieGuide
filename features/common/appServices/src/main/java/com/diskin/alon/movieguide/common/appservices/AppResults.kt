package com.diskin.alon.movieguide.common.appservices

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function

sealed class Result<T : Any> {

    data class Success<T : Any>(val data: T) : Result<T>()

    data class Error<T : Any>(val error: AppError) : Result<T>()
}

data class AppError(val description: String, val retriable: Boolean): Throwable()

fun <T : Any, R : Any> Observable<Result<T>>.mapResult(mapper: Function<T,R>): Observable<Result<R>> {
    return this.map {
        when(it) {
            is Result.Success -> Result.Success(mapper.apply(it.data))
            is Result.Error -> Result.Error(it.error)
        }
    }
}

fun <T : Any> Observable<T>.toResult(): Observable<Result<T>> {
    return this.map { toSuccessResult(it) }
        .onErrorReturn { toResultError(it) }
}

fun <T : Any> Single<T>.toSingleResult(): Single<Result<T>> {
    return this.map { toSuccessResult(it) }
        .onErrorReturn { toResultError(it) }
}

fun <T : Any> Observable<Result<T>>.toData(): Observable<T> {
    return this.flatMap {
        when(it ) {
            is Result.Success -> Observable.just(it.data)
            is Result.Error -> Observable.error(it.error)
        }
    }
}

private fun <T : Any> toSuccessResult(data: T): Result<T> {
    return Result.Success(data)
}

private fun <T : Any> toResultError(throwable: Throwable): Result<T> {
    return when(throwable) {
        is AppError -> Result.Error(throwable)
        else -> Result.Error(AppError("Unknown error",false))
    }
}