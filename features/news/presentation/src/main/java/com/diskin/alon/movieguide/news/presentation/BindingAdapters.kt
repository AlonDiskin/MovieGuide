package com.diskin.alon.movieguide.news.presentation

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.diskin.alon.movieguide.common.presentation.ImageLoader

@BindingAdapter("loadImage")
fun loadImage(imageView: ImageView, url: String?) {
    url?.let { ImageLoader.loadIntoImageView(imageView, it) }
}
