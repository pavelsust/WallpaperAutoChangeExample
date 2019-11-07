package com.funapp.wallpaperautochangeexample.activities

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.funapp.wallpaperautochangeexample.R
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.activity_wallpaper.*
import kotlinx.android.synthetic.main.activity_wallpaper.bgWallpaper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.sourcei.android.permissions.Permissions
import test.*
import test.functions.*
import test.handlers.DialogHandler
import test.handlers.ImageHandler
import test.handlers.StorageHandler
import test.handlers.WallpaperHandler
import java.io.File

class WallpaperActivity : AppCompatActivity()  , View.OnClickListener{


    private var bitmap: Bitmap? = null

    var refreshing = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper)

        if (File(cacheDir, "homescreen.jpg").exists()) {
            bitmap = StorageHandler.getBitmapFromFile((File(cacheDir, "homescreen.jpg")))
            bgWallpaper.setImageBitmap(bitmap)
        } else {
            refreshing = true
            mask.show()
            progress.show()
            getImage()
        }

        refresh.setOnClickListener(this)
        settings.setOnClickListener(this)
        setWallpaper.setOnClickListener(this)
        download.setOnClickListener(this)

        // rate us dialog

        if (!Prefs.contains(RATE)){
           DialogHandler.rateUs(this){
               Prefs.putAny(RATE , true)
               F.startWeb(this , Config.PLAY_STORE)
               DialogHandler.dismiss()
           }
        }
    }


    /**
     *  get new Images
     */

    private fun getImage() {
        ImageHandler.getBitmapWallpaper(
            this,
            "https://source.unsplash.com/random/1440x3040/?${Prefs.getString("search", "")}"
        ) {
            runOnUiThread {
                if (it != null) {
                    F.compareBitmaps(it, bitmap!!) { com ->
                        if (com) {
                            getImage() // get image again if received the same one
                        } else {
                            bitmap = it
                            bgWallpaper.setImageBitmap(it)

                            // saved bitmap into the temp directory
                            StorageHandler.storeBitmapInFile(it, File(cacheDir, "homescreen.jpg"))

                            // save bitmap in cached directory

                            val cached = File(filesDir, CACHED)
                            StorageHandler.storeBitmapInFile(it, File(cached, "${F.shortid()}.jpg"))

                            // if extra images in cached then delete them

                            F.deleteCached(this, Prefs.getString(CACHE_NUMBER, "25")!!.toInt())

                            if (Prefs.getBoolean(WALL_CHANGE, false)) {
                                WallpaperHandler.setWallpaper(this, it)
                                mask.gone()
                                refreshing = false
                            }

                        }
                    }
                }else{
                    toast("failed to fetch images")
                    mask.gone()
                    progress.gone()
                    refreshing = false
                }
            }
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            refresh.id ->{
                if (!refreshing){
                    refreshing = true
                    mask.show()
                    progress.show()
                    getImage()
                }else{
                    toast("In Progress")
                }
            }

            settings.id ->{
                openNewActivity(SettingActivity::class.java)
            }

            setWallpaper.id ->{
                Permissions.askWriteExternalStoragePermission(this){e , r ->
                    e.let {
                        toast("Kindly provide storage permissions")
                    }

                    r.let {
                        F.mkdir()
                        if (!refreshing && bitmap!=null){
                            toast("Please wait")
                            GlobalScope.launch {
                                val file = File(Config.DEFEAULT_DOWNLOAD_PATH , "${F.shortid()}.jpg")
                                StorageHandler.storeBitmapInFile(bitmap!! , file)
                                WallpaperHandler.setWallpaper(this@WallpaperActivity , bitmap!!)

                                // make available for media scanner

                                MediaScannerConnection.scanFile(this@WallpaperActivity , arrayOf(file.toString()) , arrayOf("images/jpeg") , null)
                                runOnUiThread {
                                    toast("Wallpaper applied successfully")
                                }
                            }
                        }else{
                            toast("Kindly wait for wallpaper to load")
                        }
                    }


                }
            }

            download.id ->{
                Permissions.askWriteExternalStoragePermission(this){e , r ->
                    e.let {
                        toast("Please provide storage permission")
                    }
                    r.let {
                        F.mkdir()
                        if (!refreshing && bitmap!=null){
                            toast("Please wait")
                            GlobalScope.launch {
                                val file = File(Config.DEFEAULT_DOWNLOAD_PATH , "${F.shortid()}.jpg")
                                StorageHandler.storeBitmapInFile(bitmap!! , file)

                                // make available for media scanner
                                MediaScannerConnection.scanFile(this@WallpaperActivity , arrayOf(file.toString()) , arrayOf("images/jpeg") , null)
                                runOnUiThread {
                                    toast("Image saved successfully")
                                }
                            }
                        }else{
                            toast("Kindly wait for wallpaper to load")
                        }
                    }

                }
            }
        }
    }

}
