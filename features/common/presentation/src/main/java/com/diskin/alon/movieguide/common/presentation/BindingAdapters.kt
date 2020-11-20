package com.diskin.alon.movieguide.common.presentation

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("loadImage")
fun loadImage(imageView: ImageView, url: String?) {
    url?.let { ImageLoader.loadIntoImageView(imageView, it) }
}
