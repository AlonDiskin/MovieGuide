package com.diskin.alon.movieguide.news.appservices.data

data class ArticleDto(val title: String,
                      var content: String,
                      var author: String,
                      val date: Long,
                      val imageUrl: String,
                      val articleUrl: String)
