package com.diskin.alon.movieguide.news.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark")
data class Bookmark(val articleId: String,
                    val title: String,
                    val date: Long,
                    val imageUrl: String,
                    val articleUrl: String,
                    @PrimaryKey(autoGenerate = true) val id: Int = 0)