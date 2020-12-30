package com.diskin.alon.movieguide.common.domain

/**
 * Base class for domain entities.
 */
abstract class Entity<T : Any>(val id: T) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Entity<*>

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}