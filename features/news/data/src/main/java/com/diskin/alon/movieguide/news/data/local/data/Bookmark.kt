package com.diskin.alon.movieguide.news.data.local.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark")
data class Bookmark(val articleId: String,
                    @PrimaryKey(autoGenerate = true) val id: Int = 0)