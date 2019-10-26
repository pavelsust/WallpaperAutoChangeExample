package com.funapp.wallpaperautochangeexample.handlers

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap

object WallpaperHandler {


    fun setWallpaper(context: Context , bitmap: Bitmap){
        val manager = WallpaperManager.getInstance(context)
        manager.setBitmap(bitmap)
    }

}