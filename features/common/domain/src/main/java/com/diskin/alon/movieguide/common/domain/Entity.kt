package com.diskin.alon.movieguide.common.domain

/**
 * Common entity identifier property contract.
 */
interface Entity<T : Any> {

    val id: T
}