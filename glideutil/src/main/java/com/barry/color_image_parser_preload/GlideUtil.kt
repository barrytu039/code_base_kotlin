package com.barry.color_image_parser_preload

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target


class GlideUtil {

    companion object {

        private val options = RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
            .priority(Priority.IMMEDIATE).format(DecodeFormat.PREFER_RGB_565)
        // Get a non-default Storage bucket
        private val preLoadOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).priority(Priority.IMMEDIATE).format(DecodeFormat.PREFER_RGB_565).override(
                400, 400).encodeQuality(90)

        fun load(url: Any, imageView: ImageView) {
            Glide.with(imageView)
                .load(url)
                .apply(options)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        // important to return false so the error placeholder can be placed
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        // important to return false so the error placeholder can be placed
                        return false
                    }
                })
                .into(imageView)
        }

        fun preload(url: Any, imageView: ImageView) {
            Glide.with(imageView)
                .load(url)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        // important to return false so the error placeholder can be placed
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        // important to return false so the error placeholder can be placed
                        return false
                    }
                })
                .into(imageView)
        }

        fun downloadOnlySync(context: Context, url : String) {
            Glide.with(context)
                    .downloadOnly()
                    .load(url)
                    .submit()
                    .get()
        }

        fun preloadMemory(context: Context, url : String) {
            Glide.with(context)
                    .downloadOnly()
                    .load(url)
                    .preload()
        }
    }
}