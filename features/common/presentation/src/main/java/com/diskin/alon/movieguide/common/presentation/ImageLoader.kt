package com.diskin.alon.movieguide.common.presentation

import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageLoader {

    fun loadIntoImageView(imageView: ImageView, url: String) {
        Glide
            .with(imageView.context)
            .load(url)
            .centerCrop()
            .into(imageView)
    }
}