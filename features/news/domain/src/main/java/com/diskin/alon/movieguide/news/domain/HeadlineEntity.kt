package com.diskin.alon.movieguide.news.domain

/**
 * Movie news headline entity.
 */
class HeadlineEntity(val id: String,
                     var title: String,
                     var date: Long,
                     var imageUrl: String,
                     val articleUrl: String)