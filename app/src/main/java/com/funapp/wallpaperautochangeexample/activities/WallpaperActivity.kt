package com.funapp.wallpaperautochangeexample.activities

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.funapp.wallpaperautochangeexample.R
import com.wallpaper.WallpaperApplyTask
import com.wallpaper.WallpaperItem
import com.wallpaper.WallpaperPropertiesLoaderTask
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.activity_wallpaper.*
import kotlinx.android.synthetic.main.activity_wallpaper.bgWallpaper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.sourcei.android.permissions.Permissions
import test.*
import test.functions.*
import test.handlers.DialogHandler
import test.handlers.ImageHandler
import test.handlers.StorageHandler
import test.handlers.WallpaperHandler
import java.io.File
class WallpaperActivity : AppCompatActivity(), View.OnClickListener , WallpaperPropertiesLoaderTask.CallbackWallpaper{


    private var bitmap: Bitmap? = null
    var refreshing = false

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.funapp.wallpaperautochangeexample.R.layout.activity_wallpaper)


        // check for temp image & apply
        if (File(cacheDir, "homescreen.jpg").exists()) {
            bitmap = StorageHandler.getBitmapFromFile(File(cacheDir, "homescreen.jpg"))
            bgWallpaper.setImageBitmap(bitmap)
        } else {
            // if no temp image then auto load image
            refreshing = true
            mask.show()
            progress.show()
            getImage()
        }

        wallpaper_refresh.setOnClickListener(this)
        wallpaper_settings.setOnClickListener(this)
        wallpaper_setWallpaper.setOnClickListener(this)
        wallpaper_download.setOnClickListener(this)

        // rate us dialog
        if (!Prefs.contains(RATE))
            DialogHandler.rateUs(this) {
                Prefs.putAny(RATE, true)
                F.startWeb(this, Config.PLAY_STORE)
                DialogHandler.dismiss()



            }

        //var wallpaper = WallpaperItem("http://demo-wallpaper.bollywoodgaana.com/url_lg/7566.jpg")

        //https@ //images.pexels.com/photos/67636/rose-blue-flower-rose-blooms-67636.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500


        var wallpaper = WallpaperItem("https://images.pexels.com/photos/67636/rose-blue-flower-rose-blooms-67636.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500")
        /*
        WallpaperPropertiesLoaderTask.prepare(applicationContext)
            .wallpaper(wallpaper)
            .callbackWallpaper(this)
            .start {
                if (it){

                    runOnUiThread {
                        Log.d("WALLPAPER_ITEM" , ""+it)
                    }
                }
            }
            */

        WallpaperApplyTask.prepare(this)
            .wallpaper(wallpaper)
            .to(WallpaperApplyTask.Apply.HOME_CROP_WALLPAPER())
            .start {
                Log.d("Message", "Done Wallpaper")
            }
    }

    // fab click handling
    override fun onClick(v: View) {
        when (v.id) {

            wallpaper_refresh.id -> {
                if (!refreshing) {
                    refreshing = true
                    mask.show()
                    progress.show()
                    getImage()
                } else
                    toast("In Progress")
            }

            wallpaper_settings.id -> {
                Toast.makeText(applicationContext , "Click" , Toast.LENGTH_SHORT).show()
                activityOpen(SettingActivity::class.java)
            }

            wallpaper_setWallpaper.id -> {

                // check permissions
                Permissions.askWriteExternalStoragePermission(this) { e, r ->
                    e?.let {
                        toast("kindly provide storage permissions")
                    }
                    r?.let {
                        F.mkdir()
                        if (!refreshing && bitmap != null) {
                            toast("please wait")
                            GlobalScope.launch {
                                val file = File(Config.DEFEAULT_DOWNLOAD_PATH, "${F.shortid()}.jpg")
                                StorageHandler.storeBitmapInFile(bitmap!!, file)
                                WallpaperHandler.setWallpaper(this@WallpaperActivity, bitmap!!)
                                // make available for media scanner
                                MediaScannerConnection.scanFile(this@WallpaperActivity, arrayOf(file.toString()), arrayOf("image/jpeg"), null)

                                runOnUiThread {
                                    toast("wallpaper applied successfully")
                                }

                            }
                        } else
                            toast("kindly wait for wallpaper to load")
                    }
                }
            }

            wallpaper_download.id -> {
                Permissions.askWriteExternalStoragePermission(this) { e, r ->
                    e?.let {
                        toast("kindly provide storage permissions")
                    }
                    r?.let {
                        F.mkdir()
                        if (!refreshing && bitmap != null) {
                            toast("please wait")
                            GlobalScope.launch {
                                val file = File(Config.DEFEAULT_DOWNLOAD_PATH, "${F.shortid()}.jpg")
                                StorageHandler.storeBitmapInFile(bitmap!!, file)

                                // make available for media scanner
                                MediaScannerConnection.scanFile(this@WallpaperActivity, arrayOf(file.toString()), arrayOf("image/jpeg"), null)

                                runOnUiThread {
                                    toast("image saved successfully")
                                }
                            }
                        } else
                            toast("kindly wait for wallpaper to load")
                    }
                }
            }
        }
    }

    /**
     * get new image
     */
    private fun getImage() {

        Log.d("MAINACTIVITY" , ""+bitmap)

        ImageHandler.getBitmapWallpaper(this, "https://source.unsplash.com/random/1440x3040/?${Prefs.getString("search", "")}") {
            runOnUiThread {
                if (it != null) {
                    F.compareBitmaps(it, bitmap) { com ->
                        runOnUiThread {
                            if (com)
                                getImage() // get image again if received the same one
                            else {
                                bitmap = it
                                bgWallpaper.setImageBitmap(it)

                                // save bitmap in temp directory
                                StorageHandler.storeBitmapInFile(it, File(cacheDir, "homescreen.jpg"))

                                // save bitmap in cached directory
                                val cached = File(filesDir, CACHED)
                                StorageHandler.storeBitmapInFile(it, File(cached, "${F.shortid()}.jpg"))

                                // if extra images in cached then delete them
                                F.deleteCached(this, Prefs.getString(CACHE_NUMBER, "25")!!.toInt())

                                // change wallpaper if allowed
                                if (Prefs.getBoolean(WALL_CHANGE, false))
                                    WallpaperHandler.setWallpaper(this, it)

                                mask.gone()
                                progress.gone()
                                refreshing = false
                            }
                        }
                    }
                } else { // if bitmap is null
                    toast("failed to fetch image")
                    mask.gone()
                    progress.gone()
                    refreshing = false
                }
            }
        }
    }

    fun <T> activityOpen(it:Class<T>) = Intent().apply {
        startActivity(Intent(this@WallpaperActivity , it))
    }

    override fun onPropertiesReceived(wallpaper: WallpaperItem) {
        runOnUiThread {
            Log.d("WALLPAPER_ITEM" , ""+wallpaper.mimeType+" "+wallpaper.imageDimension)
        }
    }

}
