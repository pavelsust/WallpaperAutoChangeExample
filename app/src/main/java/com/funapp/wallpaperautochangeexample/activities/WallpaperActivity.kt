package com.funapp.wallpaperautochangeexample.activities

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.funapp.wallpaperautochangeexample.R
import com.funapp.wallpaperautochangeexample.functions.F
import com.funapp.wallpaperautochangeexample.functions.show
import com.funapp.wallpaperautochangeexample.handlers.ImageHandler
import com.funapp.wallpaperautochangeexample.handlers.StorageHandler
import kotlinx.android.synthetic.main.activity_image.view.*
import kotlinx.android.synthetic.main.activity_wallpaper.*
import test.CACHED
import test.CACHE_NUMBER
import test.Prefs
import test.WALL_CHANGE
import java.io.File

class WallpaperActivity : AppCompatActivity() {


    private var bitmap:Bitmap?= null

    var refreshing = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper)

        if (File(cacheDir , "homescreen.jpg").exists()){
            bitmap = StorageHandler.getBitmapFromFile((File(cacheDir , "homescreen.jpg")))
        }else{
            refreshing = true
            mask.show()
            progress.show()


        }
    }


    /**
     *  get new Images
     */

    private fun getImage(){
        ImageHandler.getBitmapWallpaper(this , "https://source.unsplash.com/random/1440x3040/?${Prefs.getString("search", "")}"){
            runOnUiThread {
                if (it !=null){
                    F.compareBitmaps(it , bitmap!!){ com ->
                        if (com){
                            getImage() // get image again if received the same one
                        }else{
                            bitmap = it
                            bgWallpaper.setImageBitmap(it)

                            // saved bitmap into the temp directory
                            StorageHandler.storeBitmapInFile(it , File(cacheDir , "homescreen.jpg"))

                            // save bitmap in cached directory

                            val cached = File(filesDir , CACHED)
                            StorageHandler.storeBitmapInFile(it , File(cached , "${F.shortid()}.jpg"))

                            // if extra images in cached then delete them

                            F.deleteCached(this , Prefs.getString(CACHE_NUMBER, "25")!!.toInt())

                            if (Prefs.getBoolean(WALL_CHANGE , false)){
                                com.funapp.wallpaperautochangeexample.handlers.WallpaperHandler.setWallpaper
                            }

                        }
                    }
                }
            }
        }
    }
}
