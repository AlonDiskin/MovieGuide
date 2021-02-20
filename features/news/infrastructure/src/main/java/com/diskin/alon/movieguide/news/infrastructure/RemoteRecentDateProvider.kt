package com.diskin.alon.movieguide.news.infrastructure

import io.reactivex.Single
import java.util.*

interface RemoteRecentDateProvider {

    fun getDate(): Single<Date>
}