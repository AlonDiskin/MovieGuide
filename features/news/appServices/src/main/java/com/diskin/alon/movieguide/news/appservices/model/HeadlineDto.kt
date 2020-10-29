package com.diskin.alon.movieguide.news.appservices.model

data class HeadlineDto(val id: String,
                       val title: String,
                       val date: Long,
                       val imageUrl: String,
                       val articleUrl: String)