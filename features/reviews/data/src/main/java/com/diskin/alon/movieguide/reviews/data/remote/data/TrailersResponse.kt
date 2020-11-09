package com.diskin.alon.movieguide.reviews.data.remote.data

/**
 * 'The Movie DB' api response model
 */
data class TrailersResponse(val results: List<TrailerResult>) {

    data class TrailerResult(val key: String)
}