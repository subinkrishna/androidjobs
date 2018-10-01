package com.subinkrishna.ext

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

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
            object : Callback {
                override fun onSuccess() = onSuccess.invoke()
                override fun onError(e: Exception?) = Unit
            }
        }
        else -> null
    }

    Picasso.get().load(Uri.parse(url)).apply {
        if (!crossfade) noFade()
        if (placeHolderRes != -1) placeholder(placeHolderRes)
        if (errorDrawableRes != -1) error(errorDrawableRes)
        if (centerCrop) centerCrop() else centerInside()
        fit()
    }.into(this, callback)
}