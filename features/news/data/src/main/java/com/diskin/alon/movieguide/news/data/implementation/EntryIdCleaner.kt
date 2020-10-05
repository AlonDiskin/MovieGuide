package com.diskin.alon.movieguide.news.data.implementation

fun cleanId(id: String) = id.replace(Regex("[+/]"),"")