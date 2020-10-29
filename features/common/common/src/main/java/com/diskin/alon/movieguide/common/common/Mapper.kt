package com.diskin.alon.movieguide.common.common

interface Mapper<S,D> {

    fun map(source: S): D
}