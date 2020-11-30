package com.diskin.alon.movieguide.news.presentation.data

data class Article(val title: String,
                   val author: String,
                   val content: String,
                   val date: String,
                   val imageUrl: String,
                   val articleUrl: String,
                   val bookmarked: Boolean)
