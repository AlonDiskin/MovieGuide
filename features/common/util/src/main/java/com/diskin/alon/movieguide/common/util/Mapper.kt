package com.diskin.alon.movieguide.common.util

/**
 * Models mapper contract.
 *
 * @param S source model type.
 * @param D destination model type.
 */
interface Mapper<S,D : Any> {

    /**
     * Maps source model to destination type model.
     */
    fun map(source: S): D
}