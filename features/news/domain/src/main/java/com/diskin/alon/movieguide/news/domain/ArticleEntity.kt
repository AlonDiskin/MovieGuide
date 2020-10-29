package com.diskin.alon.movieguide.news.domain

data class ArticleEntity(val id: String,
                    var title: String,
                    var content: String,
                    var author: String,
                    var date: Long,
                    var imageUrl: String,
                    var articleUrl: String)