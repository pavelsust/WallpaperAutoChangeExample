package com.funapp.wallpaperautochangeexample.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.funapp.wallpaperautochangeexample.R
import com.funapp.wallpaperautochangeexample.reusables.IMAGE
import kotlinx.android.synthetic.main.activity_image.*

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


    override fun onClick(v: View?) {

    }
}
