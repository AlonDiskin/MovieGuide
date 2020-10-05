package com.diskin.alon.movieguide.news.domain

import java.util.*

class ArticleEntity(val id: String,
                    var title: String,
                    var content: String,
                    var author: String,
                    var date: Calendar,
                    var imageUrl: String,
                    val articleUrl: String)