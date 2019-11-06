package com.funapp.wallpaperautochangeexample.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.funapp.wallpaperautochangeexample.R
import com.funapp.wallpaperautochangeexample.functions.F
import com.funapp.wallpaperautochangeexample.functions.toast
import com.funapp.wallpaperautochangeexample.handlers.StorageHandler
import com.funapp.wallpaperautochangeexample.handlers.WallpaperHandler
import test.Config
import test.IMAGE
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.sourcei.android.permissions.Permissions
import java.io.File

class ImageActivity : AppCompatActivity()  , View.OnClickListener{


    private lateinit var image: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        image = BitmapFactory.decodeFile(intent.getStringExtra(IMAGE))
        bgWallpaper.setImageBitmap(image)

        download.setOnClickListener(this)
        setWallpaper.setOnClickListener(this)
        back.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            setWallpaper.id -> {
                Permissions.askWriteExternalStoragePermission(this) { e, r ->

                    e.let {
                        toast("Kindly provide storage permissions")
                    }
                    r.let {
                        F.mkdir()
                        toast("Please wait")
                        GlobalScope.launch {
                            val file = File(Config.DEFEAULT_DOWNLOAD_PATH, "${F.shortid()}.jpg")
                            StorageHandler.storeBitmapInFile(image, file)
                            WallpaperHandler.setWallpaper(this@ImageActivity, image)
                            MediaScannerConnection.scanFile(
                                this@ImageActivity,
                                arrayOf(file.toString()),
                                arrayOf("image/jpeg"),
                                null
                            )

                            runOnUiThread {
                                toast("Wallpaper set")
                            }
                        }
                    }
                }
            }

            download.id -> {
                Permissions.askWriteExternalStoragePermission(this) { e, r ->
                    e.let {
                        toast("Kindly provide storage permission")
                    }
                    r.let {
                        F.mkdir()
                        toast("Please wait")
                        GlobalScope.launch {
                            val file = File(Config.DEFEAULT_DOWNLOAD_PATH, "${F.shortid()}.jpg")
                            StorageHandler.storeBitmapInFile(image, file)
                            MediaScannerConnection.scanFile(
                                this@ImageActivity,
                                arrayOf(file.toString()),
                                arrayOf("image/jpeg"),
                                null
                            )
                            runOnUiThread {
                                toast("Image saved successfully")
                            }
                        }
                    }
                }
            }

            back.id -> {
                finish()
            }
        }
    }
}
