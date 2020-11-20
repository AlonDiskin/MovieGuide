package com.diskin.alon.movieguide.reviews.domain.value

data class MovieGenre(val name: String = "unknown") {

    init {
        require(name.isNotEmpty())
    }
}