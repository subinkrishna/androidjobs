/**
 * Copyright (C) 2019 Subinkrishna Gopi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("NOTHING_TO_INLINE")

package com.subinkrishna.ext.view

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

/**
 * Loads an image from the given URL to an [ImageView] using [Glide]
 *
 * @param url
 * @param crossfade
 * @param centerCrop
 * @param placeHolderRes
 * @param errorDrawableRes
 * @param onSuccess
 */
fun ImageView.setImageUrl(
        url: String?,
        crossfade: Boolean = true,
        centerCrop: Boolean = false,
        @DrawableRes placeHolderRes: Int = -1,
        @DrawableRes errorDrawableRes: Int = -1,
        onSuccess: (() -> Unit)? = null
) {
    if (url.isNullOrBlank()) {
        when {
            placeHolderRes != -1 -> setImageResource(placeHolderRes)
            errorDrawableRes != -1 -> setImageResource(errorDrawableRes)
            else -> setImageDrawable(null)
        }
        return
    }

    val callback = when {
        (null != onSuccess) -> {
            object : RequestListener<Drawable> {
                override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                ): Boolean {
                    onSuccess.invoke()
                    return false
                }
            }
        }
        else -> null
    }

    val requestOptions = RequestOptions().apply {
        diskCacheStrategy(DiskCacheStrategy.ALL)
        if (placeHolderRes != -1) placeholder(placeHolderRes)
        if (errorDrawableRes != -1) error(errorDrawableRes)
        if (centerCrop) centerCrop() else fitCenter()
        if (!crossfade) dontAnimate()
    }

    Glide.with(context.applicationContext)
            .load(url)
            .apply(requestOptions)
            .listener(callback)
            .into(this)
}

inline fun ImageView.setGifResource(resId: Int) {
    Glide.with(context.applicationContext)
            .load(resId)
            .into(this)
}