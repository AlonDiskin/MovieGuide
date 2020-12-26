package com.diskin.alon.movieguide.news.data.remote.data

data class FeedlyEntryResponse(val id: String,
                               val title: String?,
                               val author: String?,
                               val published: Long,
                               val visual: Visual?,
                               val originId: String,
                               val summary: Summary?
) {

    data class Visual(val url: String)

    data class Summary(var content: String)
}