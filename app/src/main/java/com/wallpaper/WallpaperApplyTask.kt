package com.wallpaper

import android.content.Context
import android.graphics.RectF
import android.util.Log
import com.danimahardhika.android.helpers.core.ColorHelper
import com.funapp.wallpaperautochangeexample.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

object WallpaperApplyTask : WallpaperPropertiesLoaderTask.CallbackWallpaper {

    var context: WeakReference<Context>? = null
    var apply: Apply? = null
    var rectF: RectF? = null
    var wallpaperItem: WallpaperItem? = null


    fun applyTask(context: Context): WallpaperApplyTask {
        this.context = WeakReference(context)
        return WallpaperApplyTask
    }

    fun to(apply: Apply): WallpaperApplyTask {
        this.apply = apply
        return this
    }

    fun wallpaper(wallpaperItem: WallpaperItem): WallpaperApplyTask {
        this.wallpaperItem = wallpaperItem
        return this
    }

    fun prepare(context: Context): WallpaperApplyTask {
        return applyTask(context)
    }

    fun start(callback: (Boolean) -> Unit) {
        GlobalScope.launch {
            var color = wallpaperItem?.color
            color = ColorHelper.getAttributeColor(context?.get(), R.attr.colorAccent)

            if (wallpaperItem?.imageDimension == null) {
                WallpaperPropertiesLoaderTask.prepare(context?.get()!!)
                    .wallpaper(wallpaperItem!!)
                    .callbackWallpaper(this@WallpaperApplyTask)
                    .start {
                        if (it) {
                            Log.d("WALLPAPER_ITEM", "" + it)

                        }
                    }
            }

        }
    }


    override fun onPropertiesReceived(wallpaper: WallpaperItem) {
        wallpaperItem = wallpaper

    }


    sealed class Apply {
        class LOCKSCREEN : Apply()
        class HOMESCREEN : Apply()
        class HOMESCREEN_LOCKSCREEN() : Apply()
    }

}