package com.wallpaper

import android.content.Context
import android.graphics.BitmapFactory
import android.net.UrlQuerySanitizer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumesAll
import kotlinx.coroutines.launch
import org.sourcei.android.permissions.utils.Config.callback
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import javax.security.auth.callback.Callback

object WallpaperPropertiesLoaderTask {


     var mWallpaper: WallpaperItem? = null
     var mCallback: WeakReference<Callback>? = null
     var mContext: WeakReference<Context>? = null

    fun prepare(context: Context , mWallpaperItem: WallpaperItem){
        this.mContext = WeakReference(context)
        this.mWallpaper = mWallpaperItem

    }




    fun start(){
        GlobalScope.launch {

            if (wallpaperItem?.imageDimension!=null && wallpaperItem?.mimeType!=null&& wallpaperItem!!.imageSize>0){
                callback(false)
            }

            var options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            var url = URL(wallpaperItem?.imageLink)

            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 15000

        }
    }
}

fun main(){
    var context: Context?
    var wallpaperItem:WallpaperItem?=null

    WallpaperPropertiesLoaderTask.run {

        prepare(context!! , wallpaperItem!!).start()
        start()
    }
}