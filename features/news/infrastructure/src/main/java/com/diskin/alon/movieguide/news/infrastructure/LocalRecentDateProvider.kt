package com.diskin.alon.movieguide.news.infrastructure

import io.reactivex.Single
import java.util.*

interface LocalRecentDateProvider {

    fun getDate(): Single<Date>
}