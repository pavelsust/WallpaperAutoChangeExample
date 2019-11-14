package com.wallpaper

import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.RectF
import android.os.Build
import android.util.Log
import com.danimahardhika.android.helpers.core.ColorHelper
import com.danimahardhika.android.helpers.core.WindowHelper
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageSize
import com.wallpaper.WallpaperApplyTask.Apply.HOMESCREEN_LOCKSCREEN
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.*
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration

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
            color = ColorHelper.getAttributeColor(context?.get(), com.funapp.wallpaperautochangeexample.R.attr.colorAccent)

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
        if (wallpaperItem?.imageDimension == null){
            Log.d("MESSAGE" , "wallpaper apply cancled")
            if (context?.get() == null) return
            if (context?.get() is Activity) {
                if ((context?.get() as Activity).isFinishing)
                    return
            }
            return
        } else {

        }

        try {
            wallpaperExecute()
        } catch (e: IllegalStateException) {

        }
    }


    fun wallpaperExecute(){

        var job = GlobalScope.launch {
            var imageSize = WallpaperHelper.getTargetSize(context?.get()!!)

            if (rectF !=null && Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
                var point: Point = WindowHelper.getScreenSize(context?.get()!!)
                val height = point.y - WindowHelper.getStatusBarHeight(context?.get()!!) - WindowHelper.getNavigationBarHeight(context?.get()!!)
                val heightFactor = imageSize.height.toFloat() / height.toFloat()
                rectF = WallpaperHelper.getScaledRestF(rectF, heightFactor, 1f)
            }

            if (rectF==null){

                /**
                 *  Create a center crop if wallpaper applied from grid. not opening the preview
                 */

                val widthScaleFactor =
                    imageSize.height.toFloat() / wallpaperItem?.imageDimension?.height?.toFloat()!!
                val side =
                    (wallpaperItem?.imageDimension?.width?.toFloat()!! * widthScaleFactor - imageSize.width.toFloat()) / 2f
                val leftRectF = 0f - side
                val rightRectF =
                    wallpaperItem?.imageDimension?.width?.toFloat()!! * widthScaleFactor - side
                val topRectF = 0f
                val bottomRectF = imageSize.height.toFloat()
                rectF = RectF(leftRectF, topRectF, rightRectF, bottomRectF)
                Log.d("Message", "created center crop rectF: $rectF")
            }

            var adjustedSize = imageSize
            Log.d("Message" , ""+adjustedSize)
            var adjustedRectF = rectF

            var scaleFactor =
                wallpaperItem?.imageDimension?.height?.toFloat()!! / imageSize.height.toFloat()


            Log.d("Message" , "Scale factor $scaleFactor")

            // scale factor is working

            if (scaleFactor >1f){

                /**
                 *  Applying original wallpaper size cause a problem
                 *  if wallpaper dimension bigger than device screen resolution
                 *  Solution: Resize wallpaper to match screen resolution
                 *
                 *
                 *
                 *  use original wallpaper size:
                 *  adjustSize = new ImageSize(width , height)
                 */

                var widthScaleFactor = imageSize.height.toFloat() / wallpaperItem?.imageDimension?.height as Float
                val adjustedWidth = java.lang.Float.valueOf(wallpaperItem?.imageDimension?.width as Float * widthScaleFactor).toInt()
                adjustedSize = ImageSize(adjustedWidth, imageSize.height)

                if (adjustedRectF != null) {

                    /**
                     *  if wallpaper crop enable , original wallpaper size should be loaded first
                     */

                    adjustedSize = ImageSize(wallpaperItem?.imageDimension?.width!!, wallpaperItem?.imageDimension?.height!!)
                    adjustedRectF = WallpaperHelper.getScaledRestF(rectF, scaleFactor, scaleFactor)

                    }
            }

            var loadedBitmap = getImageLoader().loadImageSync(wallpaperItem?.imageLink, adjustedSize, ImageConfig.getWallpaperOptions())

            if (loadedBitmap == null) {
                Log.d("Message", "Loading bitmap is null")
            }

            if (loadedBitmap != null) {
                try {
                    /**
                     *  Checking if loaded bitmap resolution supported
                     *  by yhe device
                     */

                    var bitMapTemp = Bitmap.createBitmap(
                        loadedBitmap.width,
                        loadedBitmap.height,
                        loadedBitmap.config
                    )
                    bitMapTemp.recycle()

                    var bitmap = loadedBitmap

                    if (loadedBitmap == null) {
                        Log.d("Message", "Loading bitmap is null")
                    }


                    /**
                     * final bitmap generated
                     *
                     */

                    Log.d(
                        "Message", String.format(
                            Locale.getDefault(), "generated bitmap: %d x %d ",
                            bitmap.width, bitmap.height
                        )
                    )


                    when(apply){
                        is Apply.HOMESCREEN -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                WallpaperManager.getInstance(context?.get()!!.applicationContext)
                                    .setBitmap(
                                        bitmap, null, true, WallpaperManager.FLAG_SYSTEM
                                    )

                            }

                            WallpaperManager.getInstance(context?.get()!!.applicationContext)
                                .setBitmap(bitmap)
                        }

                        is Apply.LOCKSCREEN -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                WallpaperManager.getInstance(context?.get()!!.getApplicationContext())
                                    .setBitmap(
                                        bitmap, null, true, WallpaperManager.FLAG_LOCK
                                    )
                            }
                        }

                        is HOMESCREEN_LOCKSCREEN ->{
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                WallpaperManager.getInstance(context?.get()!!.applicationContext)
                                    .setBitmap(
                                        bitmap,
                                        null,
                                        true,
                                        WallpaperManager.FLAG_LOCK or WallpaperManager.FLAG_SYSTEM
                                    )

                            }
                        }
                    }

                } catch (e: OutOfMemoryError) {
                    e.printStackTrace()

                }
            }
        }

    }

    fun getImageLoader(): ImageLoader {
        var mImageLoader: ImageLoader?=null
        if (mImageLoader == null) {
            mImageLoader = ImageLoader.getInstance()
            mImageLoader.init(ImageLoaderConfiguration.createDefault(context?.get()))
        }
        return mImageLoader!!
    }


    sealed class Apply {
        class LOCKSCREEN : Apply()
        class HOMESCREEN : Apply()
        class HOMESCREEN_LOCKSCREEN() : Apply()
    }

}
