package com.diskin.alon.movieguide.news.domain

import java.util.*

/**
 * Movie news headline entity.
 */
class HeadlineEntity(val id: String,
                     var title: String,
                     var date: Calendar,
                     var imageUrl: String)