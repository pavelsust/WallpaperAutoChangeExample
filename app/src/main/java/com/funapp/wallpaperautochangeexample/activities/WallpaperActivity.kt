package com.funapp.wallpaperautochangeexample.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.funapp.wallpaperautochangeexample.R
import com.funapp.wallpaperautochangeexample.functions.F
import com.funapp.wallpaperautochangeexample.functions.show
import com.funapp.wallpaperautochangeexample.handlers.ImageHandler
import com.funapp.wallpaperautochangeexample.handlers.StorageHandler
import com.funapp.wallpaperautochangeexample.reusables.CACHED
import com.funapp.wallpaperautochangeexample.reusables.CACHE_NUMBER
import com.funapp.wallpaperautochangeexample.reusables.Prefs
import com.funapp.wallpaperautochangeexample.reusables.WALL_CHANGE
import kotlinx.android.synthetic.main.activity_wallpaper.*
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

                            F.deleteCached(this , Prefs.getString(CACHE_NUMBER , "25")!!.toInt())

                            // change wallpaper if allowed


                        }
                    }
                }
            }
        }
    }
}
