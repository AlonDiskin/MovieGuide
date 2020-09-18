package com.diskin.alon.movieguide.news.featuretesting

import java.io.File

fun getJsonBodyFromResource(resourceName: String): String {
    val topLevelClass = object : Any() {}.javaClass.enclosingClass!!
    val jsonResource = topLevelClass.classLoader!! // javaClass.classLoader
        .getResource(resourceName)

    return File(jsonResource.toURI()).readText()
}