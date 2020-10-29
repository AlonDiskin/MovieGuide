package com.diskin.alon.movieguide.common.appservices

interface UseCase<P,R> {

    fun execute(param: P): R
}