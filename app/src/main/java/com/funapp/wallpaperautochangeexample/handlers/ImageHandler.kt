package com.funapp.wallpaperautochangeexample.handlers

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


object ImageHandler{

    fun getBitmapWallpaper(context: Context , url: String , callback: (Bitmap?) -> Unit){
        GlobalScope.launch {
            var imageBitmap = Glide.with(context)
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .submit()

            try {
                callback(imageBitmap.get())

            }catch (e:Exception){
                e.printStackTrace()
                callback(null)
            }
        }
    }
}